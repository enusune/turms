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

package im.turms.gateway.pojo.bo.session.connection;

import im.turms.common.model.dto.notification.TurmsNotification;
import im.turms.server.common.dto.CloseReason;
import im.turms.server.common.logging.core.logger.Logger;
import im.turms.server.common.logging.core.logger.LoggerFactory;
import im.turms.server.common.proto.NotificationFactory;
import im.turms.server.common.util.ProtoUtil;
import im.turms.server.common.util.ThrowableUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.net.InetSocketAddress;

/**
 * @author James Chen
 */
public class WebSocketConnection extends NetConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConnection.class);

    private final Connection connection;
    private final WebsocketOutbound out;

    protected WebSocketConnection(Connection connection, boolean isConnected) {
        super(isConnected);
        this.connection = connection;
        out = (WebsocketOutbound) connection;
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) connection.address();
    }

    /**
     * It's acceptable that the method isn't thread-safe
     */
    @Override
    public void close(CloseReason closeReason) {
        if (isConnected() && !connection.isDisposed()) {
            super.close(closeReason);
            TurmsNotification closeNotification = NotificationFactory.create(closeReason);
            ByteBuf message = ProtoUtil.getDirectByteBuffer(closeNotification);
            out.sendObject(Mono.just(new BinaryWebSocketFrame(message)), byteBuf -> true)
                    .then()
                    .doOnError(throwable -> {
                        if (!ThrowableUtil.isDisconnectedClientError(throwable)) {
                            LOGGER.error("Failed to send the close notification", throwable);
                        }
                    })
                    .retryWhen(RETRY_SEND_CLOSE_NOTIFICATION)
                    .doFinally(signal -> close())
                    .subscribe(null, t -> {
                        if (!ThrowableUtil.isDisconnectedClientError(t)) {
                            LOGGER.error("Failed to send the close notification with retries exhausted: " +
                                    RETRY_SEND_CLOSE_NOTIFICATION.maxAttempts, t);
                        }
                    });
        }
    }

    @Override
    public void close() {
        out.sendClose(WebSocketCloseStatus.NORMAL_CLOSURE.code(), null)
                .subscribe(null, t -> {
                    if (!ThrowableUtil.isDisconnectedClientError(t)) {
                        LOGGER.error("Failed to close the connection", t);
                    }
                });
    }

}