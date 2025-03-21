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

package im.turms.server.common.plugin;

import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author James Chen
 */
public abstract class PluginDescriptorFactory {

    static final String PLUGIN_ID = "id";
    static final String PLUGIN_VERSION = "version";
    static final String PLUGIN_PROVIDER = "provider";
    static final String PLUGIN_LICENSE = "license";
    static final String PLUGIN_DESCRIPTION = "description";

    public static PluginDescriptor createPluginDescriptor(Map<String, Object> properties) {
        String id = readPropertiesString(properties, PLUGIN_ID, true);
        String version = readPropertiesString(properties, PLUGIN_VERSION, false);
        String provider = readPropertiesString(properties, PLUGIN_PROVIDER, false);
        String license = readPropertiesString(properties, PLUGIN_LICENSE, false);
        String description = readPropertiesString(properties, PLUGIN_DESCRIPTION, false);
        return new PluginDescriptor(id, version, provider, license, description, null);
    }

    @Nullable
    public static String readPropertiesString(Map<String, Object> properties, String key, boolean required) {
        String value = (String) properties.get(key);
        if (StringUtils.hasText(value)) {
            return value;
        }
        if (required) {
            throw new IllegalArgumentException("Field \"%s\" cannot be blank".formatted(key));
        }
        return null;
    }
}
