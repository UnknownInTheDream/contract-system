package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.ApprovalOpinionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 审批常用语
 *
 * @author: TangLiang
 * @date: 2022/03/07 15:02
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class ApprovalOpinionsController {
    private final ApprovalOpinionsService approvalOpinionsService;

    /**
     * 加载审批常用语
     */
    @GetMapping("loadApprovalOpinions")
    @Log(title = "审批常用语管理", operateType = "加载审批常用语")
    public Map<String, Object> loadApprovalOpinions(String I_ID) {
        return BaseUtils.loadSuccess(approvalOpinionsService.loadApprovalOpinions(I_ID));
    }

    /**
     * 查询审批常用语
     */
    @GetMapping("selectApprovalOpinions")
    @Log(title = "审批常用语管理", operateType = "查询审批常用语")
    public Map<String, Object> selectApprovalOpinions(String V_PERCODE, String V_OPINIONS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = approvalOpinionsService.selectApprovalOpinions(V_PERCODE, V_OPINIONS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = approvalOpinionsService.countApprovalOpinions(V_PERCODE, V_OPINIONS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增审批常用语
     */
    @PostMapping("insertApprovalOpinions")
    @Log(title = "审批常用语管理", operateType = "新增审批常用语")
    public Map<String, Object> insertApprovalOpinions(String V_PERCODE, String V_OPINIONS) {
        if (approvalOpinionsService.insertApprovalOpinions(V_PERCODE, V_OPINIONS) == 0) {
            return BaseUtils.failed("新增审批常用语失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改审批常用语
     */
    @PostMapping("updateApprovalOpinions")
    @Log(title = "审批常用语管理", operateType = "修改审批常用语")
    public Map<String, Object> updateApprovalOpinions(String I_ID, String V_OPINIONS) {
        if (approvalOpinionsService.updateApprovalOpinions(I_ID, V_OPINIONS) == 0) {
            return BaseUtils.failed("修改审批常用语失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除审批常用语
     */
    @PostMapping("deleteApprovalOpinions")
    @Log(title = "审批常用语管理", operateType = "删除审批常用语")
    public Map<String, Object> deleteApprovalOpinions(String I_ID) {
        if (approvalOpinionsService.deleteApprovalOpinions(I_ID) == 0) {
            return BaseUtils.failed("删除审批常用语失败");
        }
        return BaseUtils.success();
    }

}
