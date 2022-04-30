package cn.newangels.system.aspect;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.annotation.event.LogEvent;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.domain.LogDocument;
import cn.newangels.system.base.EventPublishService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 日志处理
 *
 * @author: TangLiang
 * @date: 2021/4/16 13:47
 * @since: 1.0
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {
    private final ObjectMapper objectMapper;
    private final EventPublishService eventPublishService;

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(cn.newangels.common.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 拦截异常操作
     *
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(Exception e) {
        log.error("异常信息:{}", e.getMessage());
        e.printStackTrace();
    }

    /**
     * 环绕处理
     *
     * @param pjd ProceedingJoinPoint
     * @return Map<String, Object>
     * @throws Throwable
     */
    @Around("logPointCut()")
    public Map<String, Object> doAround(ProceedingJoinPoint pjd) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();
        Map<String, Object> result;
        MethodSignature signature = (MethodSignature) pjd.getSignature();
        Method method = signature.getMethod();
        // 通过 AnnotationUtils.findAnnotation 获取 Log 注解
        Log logPoint = AnnotationUtils.findAnnotation(method, Log.class);
        String operateType = "";
        String title = "";
        if (logPoint != null) {
            operateType = logPoint.operateType();
            title = logPoint.title();
        }
        Object[] args = pjd.getArgs();
        String[] paramNames = ((CodeSignature) pjd.getSignature()).getParameterNames();
        Map<String, Object> paramMap = new HashMap<>(BaseUtils.newHashMapWithExpectedSize(paramNames.length));
        String V_PERCODE = "";
        //从request获取人员编码
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.get("V_PERCODE") != null && parameterMap.get("V_PERCODE").length > 0) {
            V_PERCODE = parameterMap.get("V_PERCODE")[0];
        }
        for (int i = 0, leng = paramNames.length; i < leng; i++) {
            //HttpServletRequest等对象跳过
            if ("request".equals(paramNames[i]) || "response".equals(paramNames[i]) || "session".equals(paramNames[i]) || "multipartFile".equals(paramNames[i]) || "multipartFiles".equals(paramNames[i])) {
                continue;
            }
            paramMap.put(paramNames[i], args[i]);
        }
        String paramJson = objectMapper.writeValueAsString(paramMap);
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        //获取浏览器信息
        Browser browser = userAgent.getBrowser();
        //浏览器版本
        Version version = userAgent.getBrowserVersion();
        //获取操作系统信息
        OperatingSystem os = userAgent.getOperatingSystem();
        String traceId = MDC.get(BaseUtils.TRACE_ID);
        LogDocument logDocument = LogDocument.builder()
                .service("鞍钢合同系统系统设置")
                .title(title)
                .hostName(BaseUtils.getHostName())
                .operatePer(V_PERCODE)
                .ip(BaseUtils.getIp(request))
                .browser(browser.getName())
                .version(version != null ? version.getVersion() : "")
                .os(os.getName())
                .operateType(operateType)
                .url(BaseUtils.getUrl(request))
                .signature(pjd.getSignature().toString())
                .params(paramJson)
                //.createTime(LocalDateTime.now())
                .projectVersion("v1.0")
                .traceId(traceId)
                .build();
        try {
            result = (Map<String, Object>) pjd.proceed();
            logDocument.setSuccess(1);
        } catch (Exception e) {
            logDocument.setSuccess(0);
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            logDocument.setErrMessage(stringWriter.toString());
            eventPublishService.publishEvent(new LogEvent(this, logDocument));
            return BaseUtils.failed(e.getMessage());
        }
        eventPublishService.publishEvent(new LogEvent(this, logDocument));
        return result;
    }

}