package cn.newangels.system.publish.log;

import cn.newangels.common.annotation.event.LogEvent;
import cn.newangels.common.domain.LogDocument;
import cn.newangels.system.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * log存放至oracle
 *
 * @author: TangLiang
 * @date: 2022/1/25 9:44
 * @since: 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogToOracle {
    private final LogService logService;

    /**
     * log存放至oracle
     * 日志信息事件中日志对象为null不处理
     *
     * @param logEvent 日志信息事件
     */
    @Async
    @EventListener(condition = "#logEvent.logDocument != null")
    public void onApplicationEvent(LogEvent logEvent) {
        LogDocument logDocument = logEvent.getLogDocument();
        String V_SERVICE = logDocument.getService();
        String V_TITLE = logDocument.getTitle();
        String V_HOSTNAME = logDocument.getHostName();
        String V_OPERATEPER = logDocument.getOperatePer();
        String V_IP = logDocument.getIp();
        String V_BROWSER = logDocument.getBrowser();
        String V_VERSION = logDocument.getVersion();
        String V_OS = logDocument.getOs();
        String V_OPERATETYPE = logDocument.getOperateType();
        String V_SIGNATURE = logDocument.getSignature();
        String V_URL = logDocument.getUrl();
        String V_PARAMS = logDocument.getParams();
        String I_TRACEID = logDocument.getTraceId();
        String V_ERRMESSAGE = logDocument.getErrMessage();
        String V_PROVERSION = logDocument.getProjectVersion();
        long V_SUCCESS = logDocument.getSuccess();
        logService.insertLog(V_SERVICE, V_TITLE, V_HOSTNAME, V_OPERATEPER, V_IP, V_BROWSER, V_VERSION, V_OS, V_OPERATETYPE, V_SIGNATURE, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_ERRMESSAGE, V_PROVERSION);
    }
}
