package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.AppointToStampService;
import cn.newangels.system.service.PerToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约签章表
 *
 * @author: ll
 * @date: 2022/03/23 15:18
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class AppointToStampController {
    private final AppointToStampService appointToStampService;
    private final PerToRoleService perToRoleService;

    /**
     * 加载预约签章表
     */
    @GetMapping("loadAppointToStamp")
    @Log(title = "预约签章表管理", operateType = "加载预约签章表")
    public Map<String, Object> loadAppointToStamp(String I_ID) {
        return BaseUtils.loadSuccess(appointToStampService.loadAppointToStamp(I_ID));
    }

    /**
     * 查询预约签章表
     */
    @GetMapping("selectAppointToStamp")
    @Log(title = "预约签章表管理", operateType = "查询预约签章表")
    public Map<String, Object> selectAppointToStamp(@DateTimeFormat(pattern = "yyyy-MM-dd") Date V_BEGIN_DATE, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_END_DATE, Integer V_STAMPSTATUS, String V_PERCODE, Integer current, Integer pageSize) {
        V_END_DATE = BaseUtils.addOneDay(V_END_DATE);
        List<Map<String, Object>> perRoleList = perToRoleService.selectPerToRole(V_PERCODE, null, null, null);
        Boolean flag = true;
        for (int i = 0; i < perRoleList.size(); i++) {
            if ("contractAdministrator".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //合同管理员
                flag = false;
                break;
            }
        }
        List<Map<String, Object>> list = appointToStampService.selectAppointToStamp(V_BEGIN_DATE, V_END_DATE, V_STAMPSTATUS, V_PERCODE, flag, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = appointToStampService.countAppointToStamp(V_BEGIN_DATE, V_END_DATE, V_STAMPSTATUS, V_PERCODE, flag);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增预约签章表
     */
    @PostMapping("insertAppointToStampBatch")
    @Log(title = "预约签章表管理", operateType = "新增预约签章表")
    public Map<String, Object> insertAppointToStampBatch(@RequestParam List<String> V_CONTRACTIDLIST, String V_APPOINTDATE, String V_APPLICANT, String V_PERCODE) {
        if (appointToStampService.insertAppointToStampBatch(V_CONTRACTIDLIST, V_APPOINTDATE, V_APPLICANT, V_PERCODE) == 0) {
            return BaseUtils.failed("新增预约签章表失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改预约签章表
     */
    @PostMapping("updateAppointToStamp")
    @Log(title = "预约签章表管理", operateType = "修改预约签章表")
    public Map<String, Object> updateAppointToStamp(String I_ID, String V_APPOINTDATE, Integer V_STAMPSTATUS) {
        if (appointToStampService.updateAppointToStamp(I_ID, V_APPOINTDATE, V_STAMPSTATUS) == 0) {
            return BaseUtils.failed("修改预约签章表失败");
        }
        return BaseUtils.success();
    }

    /**
     * 批量修改预约签章表
     */
    @PostMapping("updateAppointToStampBatch")
    @Log(title = "预约签章表管理", operateType = "修改预约签章表")
    public Map<String, Object> updateAppointToStampBatch(String I_IDLIST, String V_APPOINTDATE, Integer V_STAMPSTATUS) {
        if (appointToStampService.updateAppointToStampBatch(Arrays.asList(I_IDLIST.split(",")), V_APPOINTDATE, V_STAMPSTATUS) == 0) {
            return BaseUtils.failed("修改预约签章表失败");
        }
        return BaseUtils.success();
    }

    /**
     * 批量盖章
     */
    @PostMapping("updateStampStatusBatch")
    @Log(title = "预约签章表管理", operateType = "批量盖章")
    public Map<String, Object> updateStampStatusBatch(String I_IDLIST, Integer V_STAMPSTATUS, String V_CONTRACTIDLIST) {
        if (appointToStampService.updateStampStatusBatch(Arrays.asList(I_IDLIST.split(",")), V_STAMPSTATUS, Arrays.asList(V_CONTRACTIDLIST.split(","))) == 0) {
            return BaseUtils.failed("批量盖章失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除预约签章表
     */
    @PostMapping("deleteAppointToStamp")
    @Log(title = "预约签章表管理", operateType = "删除预约签章表")
    public Map<String, Object> deleteAppointToStamp(String I_ID) {
        if (appointToStampService.deleteAppointToStamp(I_ID) == 0) {
            return BaseUtils.failed("删除预约签章表失败");
        }
        return BaseUtils.success();
    }

}
