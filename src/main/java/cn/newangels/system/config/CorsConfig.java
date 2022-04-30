package cn.newangels.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置(开发测试环境使用，生产环境配置在网关中)
 *
 * @author: TangLiang
 * @date: 2021/4/15 11:15
 * @since: 1.0
 */
@Configuration
@Profile(value = {"dev", "test"})
public class CorsConfig implements WebMvcConfigurer {
    static final String[] ORIGINS = new String[]{"GET", "POST", "PUT", "DELETE"};

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //允许所有域名进行跨域调用
                .allowedOrigins("*")
                //允许跨越发送cookie
                .allowCredentials(true)
                //放行全部原始头信息
                .allowedHeaders("*")
                //允许所有请求方法跨域调用
                .allowedMethods(ORIGINS)
                .maxAge(3600);
    }
}
