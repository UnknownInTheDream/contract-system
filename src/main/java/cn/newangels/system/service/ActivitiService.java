package cn.newangels.system.service;

import cn.newangels.system.base.BaseOrder;
import cn.newangels.system.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ActivitiService {
    /**
     * 刷新流程定义缓存
     */
    void refreshProcessDefinition();

    /**
     * 根据ID获得流程定义
     *
     * @param processDefinitionId 流程定义ID
     * @return 流程定义
     */
    Map<String, Object> loadProcessDefinition(String processDefinitionId);

    /**
     * 根据KEY获得流程定义
     *
     * @param processDefinitionKey 流程定义KEY
     * @return 流程定义
     */
    Map<String, Object> loadProcessDefinitionByProcessDefinitionKey(String processDefinitionKey);

    /**
     * 部署新流程
     *
     * @param category        流程类别
     * @param fileNameList    BPMN文件名
     * @param inputStreamList BPMN文件
     * @param operator
     * @return
     */
    ProcessDefinition insertProcessDefinition(String category, List<String> fileNameList, List<InputStream> inputStreamList, Map<String, Object> operator);

    /**
     * 部署新流程
     *
     * @param category       流程类别
     * @param multipartFiles BPMN文件
     * @return
     */
    ProcessDefinition insertProcessDefinition(String category, MultipartFile[] multipartFiles) throws IOException;

    /**
     * 启动流程
     *
     * @param processDefinitionKey
     * @param businessKey
     * @param processVariables
     * @param operator
     * @return
     */
    ProcessInstance startProcessInstance(String processDefinitionKey, String businessKey, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 查询流程
     *
     * @param processInstanceId
     * @param operator
     * @return
     */
    ProcessInstance loadProcessInstance(String processInstanceId, Map<String, Object> operator);

    /**
     * 查询流程
     *
     * @param processInstanceFilter
     * @param orderDto
     * @param page
     * @param limit
     * @param threshold
     * @param operator
     * @return
     */
    List<ProcessInstance> selectProcessInstance(ProcessInstanceFilter processInstanceFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator);

    /**
     * 统计流程个数
     *
     * @param processInstanceFilter 查询条件
     * @param operator
     * @return 流程个数
     */
    long countProcessInstance(ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator);

    /**
     * 查询已办流程
     *
     * @param processInstanceFilter
     * @param orderDto
     * @param page
     * @param limit
     * @param threshold
     * @param operator
     * @return
     */
    List<ProcessInstance> selectInvolvedProcessInstance(ProcessInstanceFilter processInstanceFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator);

    /**
     * 统计已办流程个数
     *
     * @param processInstanceFilter 查询条件
     * @param operator
     * @return 已办流程个数
     */
    long countInvolvedProcessInstance(ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator);

    /**
     * 查询活动流程
     *
     * @param processInstanceFilter
     * @param orderDto
     * @param page
     * @param limit
     * @param threshold
     * @param operator
     * @return
     */
    List<ProcessInstance> selectActiveProcessInstance(ProcessInstanceFilter processInstanceFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator);

    /**
     * 统计活动流程个数
     *
     * @param processInstanceFilter 查询条件
     * @param operator
     * @return 活动流程个数
     */
    long countActiveProcessInstance(ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator);

    /**
     * 查询草稿流程
     *
     * @param operator
     * @return
     */
    List<Map<String, Object>> selectDraftProcessInstance(Map<String, Object> operator);

    /**
     * 删除流程
     *
     * @param processInstanceId
     * @param deleteReason
     * @param operator
     */
    void deleteProcessInstance(String processInstanceId, String deleteReason, Map<String, Object> operator);

    /**
     * 删除历史流程
     *
     * @param processInstanceId
     * @param operator
     */
    void deleteHistoricProcessInstance(String processInstanceId, Map<String, Object> operator);

    /**
     * 彻底删除流程（无痕迹）
     *
     * @param processInstanceId
     * @param operator
     */
    void deleteProcessInstanceCompletely(String processInstanceId, Map<String, Object> operator);

    /**
     * 挂起流程
     *
     * @param processInstanceId
     * @param operator
     */
    void suspendProcessInstance(String processInstanceId, Map<String, Object> operator);

    /**
     * 激活流程
     *
     * @param processInstanceId
     * @param operator
     */
    void activateProcessInstance(String processInstanceId, Map<String, Object> operator);

    /**
     * 草稿化流程
     *
     * @param processInstanceId
     * @param operator
     */
    void draftProcessInstance(String processInstanceId, Map<String, Object> operator);

    /**
     * 正式化流程
     *
     * @param processInstanceId
     * @param operator
     */
    void formalProcessInstance(String processInstanceId, Map<String, Object> operator);

    /**
     * 查询任务
     *
     * @param taskId
     * @param operator
     * @return
     */
    Task loadTask(String taskId, Map<String, Object> operator);

    /**
     * 查询待办任务，用于管理待办任务页面
     *
     * @param CODE_
     * @param TITLE_
     * @param EMP_CODE_
     * @param expired
     * @param page
     * @param limit
     * @param operator
     * @return
     */
    List<Task> selectAllRunningTask(String CODE_, String TITLE_, String EMP_CODE_, Boolean expired, Integer page, Integer limit, Map<String, Object> operator);

    /**
     * 统计任务个数
     *
     * @param CODE_
     * @param TITLE_
     * @param EMP_CODE_
     * @param expired
     * @param operator
     * @return
     */
    long countAllRunningTask(String CODE_, String TITLE_, String EMP_CODE_, Boolean expired, Map<String, Object> operator);

    /**
     * 查询流程第一个待办任务
     *
     * @param processInstanceId
     * @param operator
     * @return 返回第一个待办任务
     */
    Task loadFirstTask(String processInstanceId, Map<String, Object> operator);

    /**
     * 查询流程最后一个待办任务
     *
     * @param processInstanceId
     * @param operator
     * @return
     */
    Task loadLastTask(String processInstanceId, Map<String, Object> operator);

    /**
     * 查询历史任务
     *
     * @param taskId
     * @param operator
     * @return
     */
    Task loadHistoricTask(String taskId, Map<String, Object> operator);

    /**
     * 查询任务
     *
     * @param taskFilter
     * @param orderDto
     * @param page
     * @param limit
     * @param threshold
     * @param operator
     * @return
     */
    List<Task> selectTask(TaskFilter taskFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator);

    /**
     * 统计任务个数
     *
     * @param taskFilter 查询条件
     * @param operator
     * @return 任务个数
     */
    long countTask(TaskFilter taskFilter, Map<String, Object> operator);

    /**
     * 查询历史任务
     *
     * @param processInstanceId
     * @param operator
     * @return
     */
    List<Task> selectProcessInstanceHistoricTask(String processInstanceId, Map<String, Object> operator);

    /**
     * 查询某人已完成任务
     *
     * @param assignee
     * @param operator
     * @return
     */
    List<Task> selectHistoricTask(String assignee, Integer page, Integer limit, Map<String, Object> operator);

    /**
     * 统计某人已完成任务个数
     *
     * @param assignee 某人
     * @param operator
     * @return 已完成任务个数
     */
    long countHistoricTask(String assignee, Map<String, Object> operator);

    /**
     * 统计某流程已有任务数，用于判断是否发送即时通请求
     *
     * @param processInstanceId 流程实例id
     * @param operator
     * @return 已完成任务个数
     */
    long countHistoricTaskForSendMsg(String processInstanceId, Map<String, Object> operator);

    /**
     * 转签
     *
     * @param taskId
     * @param assignee
     * @param operator
     */
    void reassignTask(String taskId, String assignee, Map<String, Object> operator);

    /**
     * 加签
     *
     * @param taskId
     * @param assigneeList
     * @param approvalMemo
     * @param operator
     */
    List<String> addCounterTask(String taskId, List<String> assigneeList, String approvalMemo, String formKey, Map<String, Object> operator);

    /**
     * 挂起任务
     *
     * @param taskId
     * @param operator
     */
    void suspendTask(String taskId, Map<String, Object> operator);

    /**
     * 激活任务
     *
     * @param taskId
     * @param operator
     */
    void activateTask(String taskId, Map<String, Object> operator);

    /**
     * 撤回到任务
     *
     * @param taskId
     * @param processVariables
     * @param operator
     */
    void withdrawToTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 完成到任务
     *
     * @param taskId
     * @param taskDefinitionKey
     * @param processVariables
     * @param operator
     */
    void completeToTask(String taskId, String taskDefinitionKey, Map<String, Object> processVariables, boolean checkAuth, Map<String, Object> operator);

    /**
     * 撤回任务
     *
     * @param taskId
     * @param processVariables
     * @param operator
     */
    void withdrawTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 完成任务
     *
     * @param taskId
     * @param processVariables
     * @param operator
     */
    void completeTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 完成任务
     *
     * @param taskId
     * @param processVariables
     * @param operator
     */
    void completeTaskN(String taskId, Map<String, Object> processVariables, String sel_emp_code, String posi_id, Map<String, Object> operator);

    /**
     * 修改任务审批意见
     *
     * @param taskId
     * @param approvalMemo
     * @param operator
     */
    void updateTaskApprovalMemo(String taskId, String approvalMemo, Map<String, Object> operator);

    /**
     * 修改历史任务审批意见
     *
     * @param taskId
     * @param approvalMemo
     * @param operator
     */
    void updateHistoricTaskApprovalMemo(String taskId, String approvalMemo, Map<String, Object> operator);

    /**
     * 修改任意历史任务审批意见
     *
     * @param taskId
     * @param approvalMemo
     * @param operator
     */
    void updateAnyHistoricTaskApprovalMemo(String taskId, String approvalMemo, Map<String, Object> operator);

    /**
     * 设置流程变量
     *
     * @param processInstanceId
     * @param processVariables
     * @param operator
     */
    void setProcessVariables(String processInstanceId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 设置Execution流程变量
     *
     * @param executionId      Execution ID
     * @param processVariables 流程变量
     * @param operator
     */
    void setExecutionVariables(String executionId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 查询下个任务定义
     *
     * @param taskId
     * @param processVariables
     * @param operator
     * @return
     */
    UserTaskDefinition loadNextUserTaskDefinition(String taskId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 查询上个任务定义
     *
     * @param taskId
     * @param processVariables
     * @param operator
     * @return
     */
    UserTaskDefinition loadPreviousUserTaskDefinition(String taskId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 查询上个任务
     *
     * @param taskId
     * @param processVariables
     * @param operator
     * @return
     */
    Task loadPreviousUserTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 查询流程定义
     *
     * @param operator
     * @return
     */
    List<ProcessDefinition> selectProcessDefinition(int threshold, Map<String, Object> operator);

    /**
     * 查询流程定义KEY
     *
     * @param operator
     * @return
     */
    List<Map<String, Object>> selectProcessDefinitionKey(Map<String, Object> operator);

    /**
     * 查询流程定义图
     *
     * @param processDefinitionKey
     * @param operator
     * @return
     */
    InputStream loadProcessDefinitionDiagram(String processDefinitionKey, Map<String, Object> operator);

    /**
     * 查询流程图
     *
     * @param processInstanceId
     * @param operator
     * @return
     */
    InputStream loadProcessInstanceDiagram(String processInstanceId, Map<String, Object> operator);

    /**
     * 修改已结公文 历史变量(文号和标题)
     *
     * @param processInstanceId
     * @param CODE_             * @param TITLE_
     */
    void updateHistoricVarinst(String processInstanceId, String CODE_, String TITLE_);

    /**
     * 根据流程定义编码查询最新流程下的第一步候选人列表
     *
     * @param processDefinitionKey
     * @param taskDefinitionKey
     * @param processVariables
     * @param operator
     * @return
     */
    List<Map<String, Object>> selectFirstTaskProcCandidate(String processDefinitionKey, String taskDefinitionKey, Map<String, Object> processVariables, Map<String, Object> operator);

    /**
     * 查询流程定义图,根据流程部署ID
     *
     * @param deploymentId
     * @return
     */
    InputStream loadProcessDefinitionDiagramByDeploymentId(String deploymentId);

    /**
     * 处理会签节点驳回
     *
     * @param taskId
     * @param approval         reject
     * @param processVariables 流程变量
     * @return
     */
    String submitCountersignTask(String taskId, String approval, Map<String, Object> processVariables) throws Exception;

    /**
     * @param taskId        任务id
     * @param endActivityId 结束节点的activitiyId
     * @throws Exception
     */
    void turnBackNew(String taskId, String endActivityId) throws Exception;

    /**
     * 获取流程变量
     *
     * @param instanceId 流程实例
     * @return Map<String, Object>
     */
    List<ActivitiVariable> getInstanceVariables(String instanceId);
}