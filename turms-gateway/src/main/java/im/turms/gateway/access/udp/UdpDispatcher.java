/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.gateway.access.udp;

import im.turms.common.constant.DeviceType;
import im.turms.common.constant.statuscode.SessionCloseStatus;
import im.turms.common.model.dto.udpsignal.UdpNotificationType;
import im.turms.common.model.dto.udpsignal.UdpRequestType;
import im.turms.common.model.dto.udpsignal.UdpSignalRequest;
import im.turms.gateway.access.udp.dto.UdpNotification;
import im.turms.gateway.access.udp.dto.UdpSignalResponseBufferPool;
import im.turms.gateway.constant.MetricsConstant;
import im.turms.gateway.constant.ThreadNameConstant;
import im.turms.gateway.pojo.bo.session.UserSession;
import im.turms.gateway.service.mediator.ServiceMediator;
import im.turms.server.common.access.common.resource.LoopResourcesFactory;
import im.turms.server.common.constant.TurmsStatusCode;
import im.turms.server.common.dto.CloseReason;
import im.turms.server.common.exception.TurmsBusinessException;
import im.turms.server.common.logging.core.logger.Logger;
import im.turms.server.common.logging.core.logger.LoggerFactory;
import im.turms.server.common.metrics.TurmsMicrometerChannelMetricsRecorder;
import im.turms.server.common.property.TurmsPropertiesManager;
import im.turms.server.common.property.env.gateway.UdpProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramPacket;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @author James Chen
 */
@Component
public class UdpDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpDispatcher.class);

    public static UdpDispatcher instance;
    private static final int REQUEST_LENGTH = Long.BYTES + Byte.BYTES * 2 + Integer.BYTES;
    @Getter
    private static boolean isEnabled;

    private final ServiceMediator serviceMediator;
    private final Sinks.Many<UdpNotification> notificationSink;
    private final Connection connection;

    public UdpDispatcher(ServiceMediator serviceMediator, TurmsPropertiesManager propertiesManager) {
        instance = this;
        UdpProperties udpProperties = propertiesManager.getLocalProperties().getGateway().getUdp();
        this.serviceMediator = serviceMediator;
        isEnabled = udpProperties.isEnabled();
        if (udpProperties.isEnabled()) {
            notificationSink = Sinks.many().unicast().onBackpressureBuffer();
            int port = udpProperties.getPort();
            String host = udpProperties.getHost();
            UdpServer udpServer = UdpServer.create()
                    .host(host)
                    .port(port)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .runOn(LoopResourcesFactory.createForServer(ThreadNameConstant.GATEWAY_UDP_PREFIX))
                    .metrics(true, () -> new TurmsMicrometerChannelMetricsRecorder(MetricsConstant.CLIENT_NETWORK, "udp"))
                    .handle((inbound, outbound) -> {
                        Flux<DatagramPacket> responseFlux = inbound.receiveObject()
                                .cast(DatagramPacket.class)
                                .flatMap(packet -> handleDatagramPackage(packet)
                                        .onErrorContinue((throwable, o) -> handleExceptionForIncomingPacket(throwable))
                                        .map(code -> new DatagramPacket(UdpSignalResponseBufferPool.get(code), packet.sender())));
                        Flux<DatagramPacket> notificationFlux = notificationSink.asFlux()
                                .map(notification -> new DatagramPacket(UdpSignalResponseBufferPool.get(notification.type()),
                                        notification.recipientAddress()));
                        Flux<DatagramPacket> outputFlux = responseFlux.mergeWith(notificationFlux);
                        outbound.sendObject(outputFlux, o -> true)
                                .then()
                                .subscribe(null, t -> LOGGER.error("Caught an error while sending object", t));
                        return Flux.never();
                    });
            try {
                connection = udpServer.bind().block();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to bind the UDP server", e);
            }
            LOGGER.info("UDP server started on {}:{}", host, port);
        } else {
            notificationSink = null;
            connection = null;
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (connection != null) {
            connection.disposeNow();
        }
    }

    public void sendSignal(InetSocketAddress address, UdpNotificationType signal) {
        if (notificationSink != null) {
            notificationSink.tryEmitNext(new UdpNotification(address, signal));
        }
    }

    private Mono<TurmsStatusCode> handleDatagramPackage(DatagramPacket packet) {
        ByteBuf content = packet.content();
        UdpSignalRequest signalRequest = parseRequest(content);
        content.release();
        InetSocketAddress senderAddress = packet.sender();
        if (signalRequest == null) {
            return Mono.just(TurmsStatusCode.INVALID_REQUEST);
        }
        long userId = signalRequest.getUserId();
        DeviceType deviceType = signalRequest.getDeviceType();
        int sessionId = signalRequest.getSessionId();
        return switch (signalRequest.getType()) {
            case HEARTBEAT -> {
                UserSession session = serviceMediator.authAndProcessHeartbeatRequest(userId, deviceType, sessionId);
                if (session == null) {
                    yield Mono.just(TurmsStatusCode.SEND_REQUEST_FROM_NON_EXISTING_SESSION);
                }
                // Update the address because it may have changed
                session.getConnection().setUdpAddress(senderAddress);
                yield Mono.just(TurmsStatusCode.OK);
            }
            case GO_OFFLINE -> {
                CloseReason reason = CloseReason.get(SessionCloseStatus.DISCONNECTED_BY_CLIENT);
                yield serviceMediator.authAndSetLocalUserDeviceOffline(userId, deviceType, reason, sessionId)
                        .thenReturn(TurmsStatusCode.OK);
            }
        };
    }

    private TurmsStatusCode handleExceptionForIncomingPacket(Throwable throwable) {
        if (throwable instanceof TurmsBusinessException exception) {
            TurmsStatusCode code = exception.getCode();
            if (code.isServerError()) {
                LOGGER.error("Failed to handle incoming package", throwable);
            }
            return code;
        } else {
            LOGGER.error("Failed to handle incoming package", throwable);
            return TurmsStatusCode.SERVER_INTERNAL_ERROR;
        }
    }

    private UdpSignalRequest parseRequest(ByteBuf byteBuf) {
        boolean isHeartbeatRequest = byteBuf.readableBytes() == REQUEST_LENGTH;
        if (isHeartbeatRequest) {
            UdpRequestType requestType = UdpRequestType.parse(byteBuf.readByte());
            if (requestType == null) {
                return null;
            }
            long userId = byteBuf.readLong();
            int deviceTypeNumber = byteBuf.readByte();
            DeviceType deviceType = DeviceType.forNumber(deviceTypeNumber);
            if (deviceType == null || deviceType == DeviceType.UNRECOGNIZED) {
                return null;
            }
            int sessionId = byteBuf.readInt();
            return new UdpSignalRequest(requestType, userId, deviceType, sessionId);
        }
        return null;
    }

}
