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

package unit.im.turms.gateway.pojo.bo.login;

import im.turms.common.constant.DeviceType;
import im.turms.common.constant.UserStatus;
import im.turms.gateway.pojo.bo.login.UserLoginInfo;
import im.turms.server.common.bo.location.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author James Chen
 */
@Getter
@AllArgsConstructor
class UserLoginInfoTests {

    private final int version = 1;
    private final Long userId = 1L;
    private final String password = "123";
    private final DeviceType loggingInDeviceType = DeviceType.ANDROID;
    private final Map<String, String> deviceDetails = Map.of("id", "ABC123456789");
    private final UserStatus userStatus = UserStatus.BUSY;
    private final Coordinates coordinates = new Coordinates(1L, 1L);
    private final String ip = "1.1.1.1";

    @Test
    void constructor_shouldReturnInstance() {
        UserLoginInfo userLoginInfo =
                new UserLoginInfo(version, userId, password, loggingInDeviceType, deviceDetails, userStatus, coordinates, ip);
        assertThat(userLoginInfo).isNotNull();
    }

    @Test
    void getters_shouldGetValues() {
        UserLoginInfo userLoginInfo =
                new UserLoginInfo(version, userId, password, loggingInDeviceType, deviceDetails, userStatus, coordinates, ip);
        assertThat(userLoginInfo.version()).isEqualTo(version);
        assertThat(userLoginInfo.userId()).isEqualTo(userId);
        assertThat(userLoginInfo.password()).isEqualTo(password);
        assertThat(userLoginInfo.loggingInDeviceType()).isEqualTo(loggingInDeviceType);
        assertThat(userLoginInfo.userStatus()).isEqualTo(userStatus);
        assertThat(userLoginInfo.coordinates()).isEqualTo(coordinates);
        assertThat(userLoginInfo.ip()).isEqualTo(ip);
        assertThat(userLoginInfo.deviceDetails()).isEqualTo(deviceDetails);
    }

}