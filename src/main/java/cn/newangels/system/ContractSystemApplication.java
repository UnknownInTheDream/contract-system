package cn.newangels.system;

import lombok.extern.slf4j.Slf4j;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
@Slf4j
public class ContractSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractSystemApplication.class, args);
        log.info("鞍钢合同系统系统模块启动成功");
    }

}
