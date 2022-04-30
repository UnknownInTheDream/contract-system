package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
 * 登录日志
 *
 * @author: TangLiang
 * @date: 2022/02/21 09:36
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class LoginLogController {
    private final LoginLogService loginLogService;

    /**
     * 查询登录日志
     */
    @GetMapping("selectLoginLog")
    @Log(title = "登录日志管理", operateType = "查询登录日志")
    public Map<String, Object> selectLoginLog(String V_OPERATEPER, String V_IP, Integer V_SUCCESS, Date START_DATE, Date END_DATE, Integer current, Integer pageSize) {
        END_DATE = BaseUtils.addOneDay(END_DATE);
        List<Map<String, Object>> list = loginLogService.selectLoginLog(V_OPERATEPER, V_IP, V_SUCCESS, START_DATE, END_DATE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = loginLogService.countLoginLog(V_OPERATEPER, V_IP, V_SUCCESS, START_DATE, END_DATE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 导出登录日志
     */
    @GetMapping("exportLoginLog")
    @Log(title = "登录日志管理", operateType = "导出登录日志")
    public void exportLoginLog(String V_OPERATEPER, String V_IP, Integer V_SUCCESS, Date START_DATE, Date END_DATE, HttpServletRequest request, HttpServletResponse response) throws IOException {
        END_DATE = BaseUtils.addOneDay(END_DATE);
        List<Map<String, Object>> list = loginLogService.selectLoginLog(V_OPERATEPER, V_IP, V_SUCCESS, START_DATE, END_DATE, null, null);
        list.forEach(item -> item.replace("V_SUCCESS", ("1".equals(item.get("V_SUCCESS")) ? "成功" : "失败")));
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(16);
        linkedHashMap.put("登录名称", "V_OPERATEPER");
        linkedHashMap.put("客户端IP", "V_IP");
        linkedHashMap.put("浏览器", "V_BROWSER");
        linkedHashMap.put("浏览器版本", "V_VERSION");
        linkedHashMap.put("操作系统", "V_OS");
        linkedHashMap.put("操作时间", "V_CREATETIME");
        linkedHashMap.put("主机名", "V_HOSTNAME");
        linkedHashMap.put("登录状态", "V_SUCCESS");
        linkedHashMap.put("错误信息", "V_ERRMESSAGE");
        BaseUtils.dealCommonExcel(wb, sheet, list, linkedHashMap);
        BaseUtils.download(wb, "登录日志.xls", request, response);
    }

}
