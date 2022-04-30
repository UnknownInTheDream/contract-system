package cn.newangels.system.config;

import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.common.util.SpringContextUtils;
import cn.newangels.common.util.cache.DelayCache;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: TangLiang
 * @date: 2022/1/14 16:50
 * @since: 1.0
 */
@Component
@RefreshScope
@Data
public class BaseConfig {

    @Value("${snowflakeIdWorker.datacenterId:1}")
    private long datacenterId;

    @Value("${snowflakeIdWorker.workerId:0}")
    private long workerId;

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(workerId, datacenterId);
    }

    @Bean
    public DelayCache<String, Map<String, Object>> delayCache() {
        return new DelayCache<>();
    }

    @Bean
    public SpringContextUtils springContextUtils() {
        return new SpringContextUtils();
    }
}
