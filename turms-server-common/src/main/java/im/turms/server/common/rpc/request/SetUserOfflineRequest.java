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

package im.turms.server.common.rpc.request;

import im.turms.common.constant.DeviceType;
import im.turms.common.constant.statuscode.SessionCloseStatus;
import im.turms.server.common.cluster.service.rpc.NodeTypeToHandleRpc;
import im.turms.server.common.cluster.service.rpc.dto.RpcRequest;
import im.turms.server.common.dto.CloseReason;
import im.turms.server.common.rpc.service.ISessionService;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author James Chen
 */
@Data
public class SetUserOfflineRequest extends RpcRequest<Boolean> {

    private static final String NAME = "setUserOffline";
    private static ISessionService sessionService;

    private final Long userId;
    private final Set<DeviceType> deviceTypes;
    private final SessionCloseStatus closeStatus;

    public SetUserOfflineRequest(
            @NotNull Long userId,
            @Nullable Set<DeviceType> deviceTypes,
            @NotNull SessionCloseStatus closeStatus) {
        this.userId = userId;
        this.deviceTypes = deviceTypes;
        this.closeStatus = closeStatus;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public NodeTypeToHandleRpc nodeTypeToRequest() {
        return NodeTypeToHandleRpc.BOTH;
    }

    @Override
    public NodeTypeToHandleRpc nodeTypeToRespond() {
        return NodeTypeToHandleRpc.GATEWAY;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        if (sessionService == null) {
            sessionService = getBean(ISessionService.class);
        }
    }

    /**
     * @return true if all the specified devices of the user were online
     */
    @Override
    public Mono<Boolean> callAsync() {
        CloseReason reason = CloseReason.get(closeStatus);
        return deviceTypes != null && !deviceTypes.isEmpty()
                ? sessionService.setLocalSessionOfflineByUserIdAndDeviceTypes(userId, deviceTypes, reason)
                : sessionService.setLocalUserOffline(userId, reason);
    }

}
