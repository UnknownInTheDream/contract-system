package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.ApprovalMemoService;
import cn.newangels.system.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.newangels.common.base.BaseUtils.DATA;

/**
 * 审批意见
 */
@RestController
@RequiredArgsConstructor
public class ApprovalMemoController {
    private final ApprovalMemoService approvalMemoService;
    private final BaseService baseService;

    @GetMapping("loadApprovalMemo")
    @Log(title = "审批意见", operateType = "根据ID查询审批意见")
    public Map<String, Object> loadApprovalMemo(String V_PERCODE, String APPROVAL_MEMO_ID_) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Map<String, Object> approvalMemo = approvalMemoService.loadApprovalMemo(APPROVAL_MEMO_ID_, operator);
        data.put(DATA, approvalMemo);
        return BaseUtils.success(data);
    }

    @GetMapping("selectApprovalMemo")
    @Log(title = "审批意见", operateType = "查询审批意见")
    public Map<String, Object> selectApprovalMemo(String V_PERCODE, String PROC_INST_ID_, String BIZ_ID_, String ASSIGNEE_CODE_, String APPROVAL_MEMO_STATUS_, Boolean ignoreEmptyDispatch, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        List<Map<String, Object>> approvalMemoList = approvalMemoService.selectApprovalMemo(PROC_INST_ID_, BIZ_ID_, ASSIGNEE_CODE_, APPROVAL_MEMO_STATUS_, ignoreEmptyDispatch, current, pageSize, operator);
        return BaseUtils.success(approvalMemoList);
    }

}
