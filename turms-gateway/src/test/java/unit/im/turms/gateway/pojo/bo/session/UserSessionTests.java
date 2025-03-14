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

package unit.im.turms.gateway.pojo.bo.session;

import im.turms.common.constant.DeviceType;
import im.turms.gateway.pojo.bo.session.UserSession;
import im.turms.server.common.bo.location.Coordinates;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author James Chen
 */
class UserSessionTests {

    private final int version = 1;
    private final Long userId = 1L;
    private final DeviceType deviceType = DeviceType.ANDROID;
    private final Map<String, String> deviceDetails = Collections.emptyMap();
    private final Coordinates coordinates = new Coordinates(1F, 1F);

    @Test
    void constructor_shouldReturnInstance() {
        UserSession userSession = new UserSession(
                version,
                userId,
                deviceType,
                deviceDetails,
                coordinates);
        assertThat(userSession).isNotNull();
    }

    @Test
    void getters_shouldGetValues() {
        UserSession userSession = new UserSession(
                version,
                userId,
                deviceType,
                deviceDetails,
                coordinates);
        assertThat(userSession.getDeviceType()).isEqualTo(deviceType);
        assertThat(userSession.getLoginCoordinates()).isEqualTo(coordinates);
    }

}
