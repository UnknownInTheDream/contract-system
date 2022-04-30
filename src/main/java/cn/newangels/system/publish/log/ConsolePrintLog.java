package cn.newangels.system.publish.log;

import cn.newangels.common.annotation.event.LogEvent;
import cn.newangels.common.domain.LogDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 控制台打印日志
 *
 * @author: TangLiang
 * @date: 2021/4/16 14:44
 * @since: 1.0
 */
@Service
@Slf4j
public class ConsolePrintLog {

    /**
     * 控制台打印日志
     * 日志信息事件中日志对象为null不处理
     *
     * @param logEvent 日志信息事件
     */
    @Async
    @EventListener(condition = "#logEvent.logDocument != null")
    public void onApplicationEvent(LogEvent logEvent) {
        LogDocument logDocument = logEvent.getLogDocument();
        if (logDocument.getSuccess() == 0) {
            log.error("traceid: [{}] 模块名: [{}]  操作类型: [{}]  IP: [{}]  URL: [{}]  方法:[{}]  参数: [{}]  操作人: [{}] 错误信息: [{}]", logDocument.getTraceId(), logDocument.getTitle(), logDocument.getOperateType(), logDocument.getIp(), logDocument.getUrl(), logDocument.getSignature(), logDocument.getParams(), logDocument.getOperatePer(), logDocument.getErrMessage());
        } else {
            log.info("模块名: [{}]  操作类型: [{}]  IP: [{}]  URL: [{}]  方法:[{}]  参数: [{}]  操作人: [{}]", logDocument.getTitle(), logDocument.getOperateType(), logDocument.getIp(), logDocument.getUrl(), logDocument.getSignature(), logDocument.getParams(), logDocument.getOperatePer());
        }
    }
}
