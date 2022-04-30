package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.LogService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统日志
 *
 * @author: TangLiang
 * @date: 2022/01/20 09:35
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    /**
     * 加载日志详情信息
     */
    @GetMapping("loadLog")
    @Log(title = "系统日志", operateType = "加载日志详情信息")
    public Map<String, Object> loadLog(String I_ID) {
        return BaseUtils.loadSuccess(logService.loadLog(I_ID));
    }

    /**
     * 查询系统日志
     */
    @GetMapping("selectLog")
    @Log(title = "系统日志", operateType = "查询系统日志")
    public Map<String, Object> selectLog(String V_SERVICE, String V_TITLE, String V_OPERATEPER, String V_IP, String V_OPERATETYPE, Integer V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_PROVERSION, Date START_DATE, Date END_DATE, Integer current, Integer pageSize) {
        END_DATE = BaseUtils.addOneDay(END_DATE);
        List<Map<String, Object>> list = logService.selectLog(V_SERVICE, V_TITLE, V_OPERATEPER, V_IP, V_OPERATETYPE, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_PROVERSION, START_DATE, END_DATE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = logService.countLog(V_SERVICE, V_TITLE, V_OPERATEPER, V_IP, V_OPERATETYPE, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_PROVERSION, START_DATE, END_DATE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 导出日志
     */
    @GetMapping("exportLog")
    @Log(title = "系统日志", operateType = "导出系统日志")
    public void exportLog(String V_SERVICE, String V_TITLE, String V_OPERATEPER, String V_IP, String V_OPERATETYPE, Integer V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_PROVERSION, @DateTimeFormat(pattern = "yyyy-MM-dd") Date START_DATE, @DateTimeFormat(pattern = "yyyy-MM-dd") Date END_DATE, HttpServletRequest request, HttpServletResponse response) throws IOException {
        END_DATE = BaseUtils.addOneDay(END_DATE);
        List<Map<String, Object>> list = logService.selectLog(V_SERVICE, V_TITLE, V_OPERATEPER, V_IP, V_OPERATETYPE, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_PROVERSION, START_DATE, END_DATE, null, null);
        list.forEach(item -> item.replace("V_SUCCESS", ("1".equals(item.get("V_SUCCESS")) ? "成功" : "失败")));
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(16);
        linkedHashMap.put("服务名", "V_SERVICE");
        linkedHashMap.put("模块名", "V_TITLE");
        linkedHashMap.put("操作类型", "V_OPERATETYPE");
        linkedHashMap.put("操作人", "V_OPERATEPER");
        linkedHashMap.put("操作时间", "V_CREATETIME");
        linkedHashMap.put("客户端IP", "V_IP");
        linkedHashMap.put("状态", "V_SUCCESS");
        linkedHashMap.put("traceid", "I_TRACEID");
        BaseUtils.dealCommonExcel(wb, sheet, list, linkedHashMap);
        BaseUtils.download(wb, "系统日志.xls", request, response);
    }
}
