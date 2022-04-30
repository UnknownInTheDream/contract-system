package cn.newangels.system.service.impl;

import cn.newangels.system.dto.ProcessInstance;
import cn.newangels.system.dto.Task;
import cn.newangels.system.service.ActivitiService;
import cn.newangels.system.service.ApproveService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ApproveServiceImpl implements ApproveService {
    private final ActivitiService activitiService;

    @Override
    public Map<String, String> startApproval(String bizId, String ASSIGNEE_, String PROCESS_DEFINITION_KEY_, Map<String, Object> processVariables, Map<String, Object> operator) {
        String EMP_CODE_ = (String) operator.get("EMP_CODE_");
        String ORG_CODE_ = (String) operator.get("ORG_CODE_");
        String ROLE_ID_ = (String) operator.get("ROLE_ID_");
        processVariables.put("initEmpCode", EMP_CODE_);
        processVariables.put("initOrgCode", ORG_CODE_);
        processVariables.put("initRoleId", ROLE_ID_);
        processVariables.put("bizId", bizId);
        //启动流程
        ProcessInstance processInstance = activitiService.startProcessInstance(PROCESS_DEFINITION_KEY_, bizId, processVariables, operator);
        //更新流程信息
        String processInstanceId = processInstance.getProcessInstanceId();
        //获取第一个待办任务
        Task task = activitiService.loadFirstTask(processInstanceId, operator);
        String taskId = task.getTaskId();
        //完成第一步节点
        processVariables.put("approval", "approve");
        processVariables.put("assignee", ASSIGNEE_);
        activitiService.completeTask(taskId, processVariables, operator);
        //组合返回数据
        Map<String, String> map = new HashMap<>();
        map.put("processInstanceId", processInstanceId);
        map.put("taskId", taskId);
        return map;
    }

    @Override
    public ProcessInstance completeTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, String APPROVAL_MEMO_, Map<String, Object> operator, Task task) {
        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put("approval", APPROVAL_);
        if (StringUtils.isNotEmpty(ASSIGNEE_)) {
            processVariables.put("assignee", ASSIGNEE_);
        }
        //完成任务
        activitiService.completeTask(TASK_ID_, processVariables, operator);
        return activitiService.loadProcessInstance(task.getProcessInstanceId(), operator);
    }

    @Override
    public ProcessInstance withDrawContractTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, String APPROVAL_MEMO_, Map<String, Object> operator, Task task) {
        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put("approval", APPROVAL_);
        if (StringUtils.isNotEmpty(ASSIGNEE_)) {
            processVariables.put("assignee", ASSIGNEE_);
        }
        //完成任务
        activitiService.withdrawTask(TASK_ID_, processVariables, operator);
        return activitiService.loadProcessInstance(task.getProcessInstanceId(), operator);
    }
}
