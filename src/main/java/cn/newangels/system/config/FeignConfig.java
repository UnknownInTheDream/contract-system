package cn.newangels.system.config;

import cn.newangels.common.base.BaseUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

/**
 * feign拦截器配置
 *
 * @author: TangLiang
 * @date: 2022/2/25 15:55
 * @since: 1.0
 */
@Configuration
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String traceId = MDC.get(BaseUtils.TRACE_ID);
        //当前线程调用中有traceId，则将该traceId进行透传
        if (traceId != null) {
            requestTemplate.header(BaseUtils.TRACE_ID, traceId);
        }
    }
}
