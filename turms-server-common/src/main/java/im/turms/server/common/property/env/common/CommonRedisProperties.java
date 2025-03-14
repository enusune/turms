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

package im.turms.server.common.property.env.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import im.turms.server.common.redis.RedisProperties;
import im.turms.server.common.redis.SimpleRedisProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.data.annotation.Transient;

/**
 * @author James Chen
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class CommonRedisProperties {

    @JsonIgnore
    @NestedConfigurationProperty
    @Transient
    private RedisProperties session = new RedisProperties();

    @JsonIgnore
    @NestedConfigurationProperty
    @Transient
    private RedisProperties location = new RedisProperties();

    @JsonIgnore
    @NestedConfigurationProperty
    @Transient
    private SimpleRedisProperties ipBlocklist = new SimpleRedisProperties();

    @JsonIgnore
    @NestedConfigurationProperty
    @Transient
    private SimpleRedisProperties userIdBlocklist = new SimpleRedisProperties();

}