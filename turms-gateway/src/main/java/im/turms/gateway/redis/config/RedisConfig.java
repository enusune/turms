package im.turms.gateway.redis.config;

import im.turms.server.common.property.TurmsPropertiesManager;
import im.turms.server.common.redis.CommonRedisConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @author James Chen
 */
@Configuration
public class RedisConfig extends CommonRedisConfig {

    protected RedisConfig(TurmsPropertiesManager turmsPropertiesManager) {
        super(turmsPropertiesManager.getLocalProperties().getGateway().getRedis(),
                turmsPropertiesManager.getLocalProperties().getLocation().isTreatUserIdAndDeviceTypeAsUniqueUser());
    }

}