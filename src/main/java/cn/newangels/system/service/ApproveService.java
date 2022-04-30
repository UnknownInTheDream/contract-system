package cn.newangels.system.service;

import cn.newangels.system.dto.ProcessInstance;
import cn.newangels.system.dto.Task;

import java.util.Map;

/**
 * 审批公用方法
 */
public interface ApproveService {
    /**
     * 开启审批
     *
     * @param bizId                   业务主键
     * @param ASSIGNEE_               候选人
     * @param PROCESS_DEFINITION_KEY_ 流程定义key
     * @param operator                操作人
     * @return
     */
    Map<String, String> startApproval(String bizId, String ASSIGNEE_, String PROCESS_DEFINITION_KEY_, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 审批
     *
     * @param TASK_ID_       子流程ID
     * @param APPROVAL_
     * @param ASSIGNEE_      候选人
     * @param APPROVAL_MEMO_ 审批语
     * @param operator       操作人
     * @param task           流程任务
     * @return
     */
    ProcessInstance completeTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, String APPROVAL_MEMO_, Map<String, Object> operator, Task task);

    /**
     * 撤审
     *
     * @param TASK_ID_       子流程ID
     * @param APPROVAL_
     * @param ASSIGNEE_      候选人
     * @param APPROVAL_MEMO_ 审批语
     * @param operator       操作人
     * @param task           流程任务
     * @return
     */
    ProcessInstance withDrawContractTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, String APPROVAL_MEMO_, Map<String, Object> operator, Task task);

}
