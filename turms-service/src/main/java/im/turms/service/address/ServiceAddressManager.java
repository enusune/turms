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

package im.turms.service.address;

import im.turms.server.common.address.BaseServiceAddressManager;
import im.turms.server.common.address.PublicIpManager;
import im.turms.server.common.property.TurmsProperties;
import im.turms.server.common.property.TurmsPropertiesManager;
import im.turms.server.common.property.env.common.AddressProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

/**
 * @author James Chen
 */
@Component
public class ServiceAddressManager extends BaseServiceAddressManager {

    public ServiceAddressManager(
            ServerProperties adminApiServerProperties,
            PublicIpManager publicIpManager,
            TurmsPropertiesManager turmsPropertiesManager) {
        super(adminApiServerProperties, publicIpManager, turmsPropertiesManager);
    }

    @Override
    protected AddressProperties getAdminAddressProperties(TurmsProperties properties) {
        return properties.getService().getAdminApi().getAddress();
    }

}