package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.dto.ActivitiVariable;
import cn.newangels.system.dto.Task;
import cn.newangels.system.service.ActivitiService;
import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.newangels.common.base.BaseUtils.DATA;

/**
 * 完成流程公用接口
 */
@RestController
@RequiredArgsConstructor
public class ApproveController {
    private final ActivitiService activitiService;
    private final BaseService baseService;
    private final ContractService contractService;

    @PostMapping("completeApprove")
    @Log(title = "审批流程", operateType = "审批流程")
    public Map<String, Object> completeApprove(String V_PERCODE, String TASK_ID_, String APPROVAL_, String ASSIGNEE_, @RequestParam(value = "ASSIGNEE_LIST", required = false) ArrayList<String> ASSIGNEE_LIST, String APPROVAL_MEMO_) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        //获取指定业务流程
        Task task = activitiService.loadTask(TASK_ID_, operator);
        //获取流程定义key
        String processDefinitionKey = task.getProcessDefinitionKey();
        int complete = 0;
        if (processDefinitionKey.equals("BidNegotiation")) {     //TODO processDefinitionKey
            complete = contractService.completeContractInTask(TASK_ID_, APPROVAL_, ASSIGNEE_, ASSIGNEE_LIST, APPROVAL_MEMO_, operator, task);
        }
        if (complete == 0) {
            throw new RuntimeException("流程完成失败");
        }
        return BaseUtils.success();
    }

    @PostMapping("completeApproveBatch")
    @Log(title = "批量审批流程", operateType = "批量审批流程")
    public Map<String, Object> completeApproveBatch(String V_PERCODE, @RequestParam(value = "TASK_ID_LIST", required = false) ArrayList<String> TASK_ID_LIST, String APPROVAL_, String ASSIGNEE_, @RequestParam(value = "ASSIGNEE_LIST", required = false) ArrayList<String> ASSIGNEE_LIST, String APPROVAL_MEMO_) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        for (String TASK_ID_ : TASK_ID_LIST) {
            //获取指定业务流程
            Task task = activitiService.loadTask(TASK_ID_, operator);
            //获取流程定义key
            String processDefinitionKey = task.getProcessDefinitionKey();
            int complete = 0;
            if (processDefinitionKey.equals("BidNegotiation")) {     //TODO processDefinitionKey
                complete = contractService.completeContractInTask(TASK_ID_, APPROVAL_, ASSIGNEE_, ASSIGNEE_LIST, APPROVAL_MEMO_, operator, task);
            }
            if (complete == 0) {
                throw new RuntimeException("流程完成失败");
            }
        }
        return BaseUtils.success();
    }

    @PostMapping("withDrawApprove")
    @Log(title = "撤审流程", operateType = "撤审流程")
    public Map<String, Object> withDrawApprove(String V_PERCODE, String TASK_ID_, String APPROVAL_, String ASSIGNEE_, @RequestParam(value = "ASSIGNEE_LIST", required = false) ArrayList<String> ASSIGNEE_LIST, String APPROVAL_MEMO_) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        //获取指定业务流程
        Task task = activitiService.loadTask(TASK_ID_, operator);
        //获取流程定义key
        String processDefinitionKey = task.getProcessDefinitionKey();
        int complete = 0;
        if (processDefinitionKey.equals("BidNegotiation")) {     //TODO processDefinitionKey
            complete = contractService.withdrawContractInTask(TASK_ID_, APPROVAL_, ASSIGNEE_, ASSIGNEE_LIST, APPROVAL_MEMO_, operator, task);
        }
        if (complete == 0) {
            throw new RuntimeException("流程完成失败");
        }
        return BaseUtils.success();
    }

    //查询审批任务是否有变更
    @GetMapping("selectUserTaskDefinition")
    @Log(title = "审批流程", operateType = "查询审批任务")
    public Map<String, Object> selectUserTaskDefinition(String V_PERCODE, String taskId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Task task = activitiService.loadTask(taskId, operator);
        //1为任务存在没有被驳回,2表示任务已经被驳回
        if (task != null) {
            data.put("task", "1");
        } else {
            data.put("task", "2");
        }
        return BaseUtils.success(data);
    }

    @GetMapping("suspendContract")
    @Log(title = "审批流程", operateType = "挂起流程")
    public Map<String, Object> suspendContract(String V_PERCODE, String processInstanceId, String SUSPEND_REASON_) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        //增加挂起原因
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("suspendReason", SUSPEND_REASON_);
        activitiService.setProcessVariables(processInstanceId, processVariables, operator);
        activitiService.suspendProcessInstance(processInstanceId, operator);
        return BaseUtils.success();
    }

    @PostMapping("activateContract")
    @Log(title = "审批流程", operateType = "激活流程")
    public Map<String, Object> activateContract(String V_PERCODE, String processInstanceId) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        //激活流程
        activitiService.activateProcessInstance(processInstanceId, operator);
        return BaseUtils.success();
    }

    //通过流程实例id获取挂起原因
    @GetMapping("viewSuspendReason")
    @Log(title = "审批流程", operateType = "获取挂起原因")
    public Map<String, Object> viewSuspendReason(String processInstanceId) {
        Map<String, Object> data = new HashMap<>();

        String suspend_reason = "";
        List<ActivitiVariable> proVariablesList = activitiService.getInstanceVariables(processInstanceId);
        if (proVariablesList != null && proVariablesList.size() > 0) {
            for (int i = 0; i < proVariablesList.size(); i++) {
                if (proVariablesList.get(i).getName() != null && !"".equals(proVariablesList.get(i).getName())) {
                    if (proVariablesList.get(i).getName().equals("suspendReason")) {
                        suspend_reason = proVariablesList.get(i).getValue().toString();
                    }
                }
            }
        }
        data.put(DATA, suspend_reason);
        return BaseUtils.success(data);
    }
}
