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

package im.turms.gateway;

import im.turms.server.common.BaseTurmsApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Responsibilities:
 * <p>
 * For users: 1. Authentication; 2. Session representation; 3. Push notifications; 4. Backpressure
 * <p>
 * For monitoring: 1. Logging; 2. Request tracing
 * <p>
 * For services: 1. Service load balancing
 *
 * @author James Chen
 */
@SpringBootApplication(
        scanBasePackages = {"im.turms.gateway", "im.turms.server.common"},
        proxyBeanMethods = false)
public class TurmsGatewayApplication extends BaseTurmsApplication {

    public static void main(String[] args) {
        bootstrap(TurmsGatewayApplication.class, args);
    }

}