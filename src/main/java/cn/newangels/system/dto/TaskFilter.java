package cn.newangels.system.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class TaskFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String taskName;
    private String parentTaskId;
    private String taskDefinitionKey;
    private String executionId;
    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String businessKey;
    private String assignee;
    private String assigneeLike;
    private Date taskCreatedBefore;
    private Date taskCreatedAfter;
    private Date taskDueBefore;
    private Date taskDueAfter;
    private Integer taskPriority;
    private Integer suspensionState;
    private String tenantId;
    private Map<String, Object> processVariables;

    public TaskFilter() {
    }

    /**
     * 获得任务ID
     *
     * @return
     */
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获得任务名称
     *
     * @return
     */
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * 获得父任务ID
     *
     * @return
     */
    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    /**
     * 获得任务定义KEY
     *
     * @return
     */
    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    /**
     * 获得执行ID
     *
     * @return
     */
    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    /**
     * 获得流程实例ID
     *
     * @return
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /**
     * 获得流程定义ID
     *
     * @return
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /**
     * 获得流程定义KEY
     *
     * @return
     */
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    /**
     * 获得业务主键
     *
     * @return
     */
    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    /**
     * 获得指派人
     *
     * @return
     */
    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    /**
     * 获得模糊查询指派人
     *
     * @return
     */
    public String getAssigneeLike() {
        return assigneeLike;
    }

    /**
     * 需要设置前后的%
     *
     * @param assigneeLike
     */
    public void setAssigneeLike(String assigneeLike) {
        this.assigneeLike = assigneeLike;
    }

    /**
     * 获得查询条件,任务创建早于时间
     *
     * @return
     */
    public Date getTaskCreatedBefore() {
        return taskCreatedBefore;
    }

    public void setTaskCreatedBefore(Date taskCreatedBefore) {
        this.taskCreatedBefore = taskCreatedBefore;
    }

    /**
     * 获得查询条件,任务创建晚于时间
     *
     * @return
     */
    public Date getTaskCreatedAfter() {
        return taskCreatedAfter;
    }

    public void setTaskCreatedAfter(Date taskCreatedAfter) {
        this.taskCreatedAfter = taskCreatedAfter;
    }

    /**
     * 获得查询条件,任务截止早于时间
     *
     * @return
     */
    public Date getTaskDueBefore() {
        return taskDueBefore;
    }

    public void setTaskDueBefore(Date taskDueBefore) {
        this.taskDueBefore = taskDueBefore;
    }

    /**
     * 获得查询条件,任务截止晚于时间
     *
     * @return
     */
    public Date getTaskDueAfter() {
        return taskDueAfter;
    }

    public void setTaskDueAfter(Date taskDueAfter) {
        this.taskDueAfter = taskDueAfter;
    }

    /**
     * 获得任务优先级
     *
     * @return
     */
    public Integer getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(Integer taskPriority) {
        this.taskPriority = taskPriority;
    }

    /**
     * 获得挂起状态
     *
     * @return
     */
    public Integer getSuspensionState() {
        return suspensionState;
    }

    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }

    /**
     * 获得Tenant ID
     *
     * @return
     */
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 获得流程变量
     *
     * @return
     */
    public Map<String, Object> getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }
}
