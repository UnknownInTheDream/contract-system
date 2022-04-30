package cn.newangels.system.config;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * nacos客户端注册至服务端时，更改服务详情中的元数据
 *
 * @author TangLiang
 */
@Slf4j
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class})
public class NacosDiscoveryClientConfiguration {

    /**
     * 程序环境
     */
    @Value("${spring.profiles.active:prod}")
    private String profiles;

    /**
     * 取操作系统版本
     */
    @Value("#{systemProperties['os.name']}")
    private String systemName;

    /**
     * 系统部署路径
     */
    @Value("#{systemProperties['user.dir']}")
    private String systemDir;

    @Bean
    @ConditionalOnMissingBean
    public NacosDiscoveryProperties nacosProperties() {
        return new NacosDiscoveryProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties nacosProperties) {
        //更改服务详情中的元数据，增加服务注册时间
        nacosProperties.getMetadata().remove("preserved.register.source");
        nacosProperties.getMetadata().put("startup.time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        nacosProperties.getMetadata().put("env", profiles);
        nacosProperties.getMetadata().put("systemName", systemName);
        nacosProperties.getMetadata().put("systemDir", systemDir);
        nacosProperties.getMetadata().put("jvmParams", runtimeParameters());
        return new NacosWatch(nacosServiceManager, nacosProperties);
    }

    //获取程序运行jvm参数
    public String runtimeParameters() {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        List<String> aList = bean.getInputArguments();
        StringJoiner sj = new StringJoiner(" ");
        for (String l : aList) {
            sj.add(l);
        }
        return sj.toString();
    }

}
