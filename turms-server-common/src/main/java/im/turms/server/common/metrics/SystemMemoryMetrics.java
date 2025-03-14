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

package im.turms.server.common.metrics;

import com.sun.management.OperatingSystemMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.lang.management.ManagementFactory;

/**
 * @author James Chen
 */
public class SystemMemoryMetrics implements MeterBinder {

    private final OperatingSystemMXBean operatingSystemMXBean;
    private final long totalMemoryBytes;

    public SystemMemoryMetrics() {
        operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        totalMemoryBytes = operatingSystemMXBean.getTotalMemorySize();
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("system.memory.total", () -> totalMemoryBytes)
                .baseUnit("bytes")
                .register(registry);
        Gauge.builder("system.memory.free", operatingSystemMXBean::getFreeMemorySize)
                .baseUnit("bytes")
                .register(registry);

        Gauge.builder("system.memory.swap.total", operatingSystemMXBean::getTotalSwapSpaceSize)
                .baseUnit("bytes")
                .register(registry);
        Gauge.builder("system.memory.swap.free", operatingSystemMXBean::getFreeSwapSpaceSize)
                .baseUnit("bytes")
                .register(registry);
    }

}
