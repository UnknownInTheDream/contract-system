package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.MajorToFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 专业流程关系
 *
 * @author: TangLiang
 * @date: 2022/03/02 16:59
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class MajorToFlowController {
    private final MajorToFlowService majorToFlowService;

    /**
     * 查询专业流程关系
     */
    @GetMapping("selectMajorToFlow")
    @Log(title = "专业流程关系管理", operateType = "查询专业流程关系")
    public Map<String, Object> selectMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = majorToFlowService.selectMajorToFlow(V_MAJORID, V_CONTYPE, V_FLOWID, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = majorToFlowService.countMajorToFlow(V_MAJORID, V_CONTYPE, V_FLOWID);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增专业流程关系
     */
    @PostMapping("insertMajorToFlow")
    @Log(title = "专业流程关系管理", operateType = "新增专业流程关系")
    public Map<String, Object> insertMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID) {
        if (majorToFlowService.insertMajorToFlow(V_MAJORID, V_CONTYPE, V_FLOWID) == 0) {
            return BaseUtils.failed("新增专业流程关系失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除专业流程关系
     */
    @PostMapping("deleteMajorToFlow")
    @Log(title = "专业流程关系管理", operateType = "删除专业流程关系")
    public Map<String, Object> deleteMajorToFlow(String I_ID) {
        if (majorToFlowService.deleteMajorToFlow(I_ID) == 0) {
            return BaseUtils.failed("删除专业流程关系失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除专业流程关系表
     */
    @PostMapping("deleteMajorFlow")
    @Log(title = "专业流程关系表管理", operateType = "删除专业流程关系表")
    public Map<String, Object> deleteMajorFlow(String V_MAJORID, String V_CONTYPE) {
        if (majorToFlowService.deleteMajorFlow(V_MAJORID,V_CONTYPE) == 0) {
            return BaseUtils.failed("删除专业流程关系表失败");
        }
        return BaseUtils.success();
    }
}
