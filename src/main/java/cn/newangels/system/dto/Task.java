package cn.newangels.system.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_RUNNING = "运行中";
    public static final String STATUS_FINISHED = "已完结";

    public static final Integer SUSPENSIONSTATE_ACTIVE = 1;
    public static final Integer SUSPENSIONSTATE_SUSPENDED = 2;
    public static final String COUNTERTASK = "COUNTERTASK";

    private String taskId;
    private String taskName;
    private String parentTaskId;
    private String taskDefinitionKey;
    private String executionId;
    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private String businessKey;
    private String formKey;
    private String startUser;
    private String startUserName;
    private String assignee;
    private String assigneeName;
    private String assigneeOrgId;
    private String assigneeOrgName;
    private Date processInstanceCreateTime;
    private Date createTime;
    private Date endTime;
    private Date dueDate;
    private Integer priority;
    private Integer suspensionState;
    private String approvalMemo;
    private String tenantId;
    private String status;
    private Map<String, Object> processVariables;
    private List<Map<String, Object>> candidateList;
    private List<Task> children;

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
     * 获得流程定义名称
     *
     * @return
     */
    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
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
     * 获得页面KEY
     *
     * @return
     */
    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    /**
     * 获得启动用户
     *
     * @return
     */
    public String getStartUser() {
        return startUser;
    }

    public void setStartUser(String startUser) {
        this.startUser = startUser;
    }

    /**
     * 获得启动用户名称
     *
     * @return
     */
    public String getStartUserName() {
        return startUserName;
    }

    public void setStartUserName(String startUserName) {
        this.startUserName = startUserName;
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
     * 获得指派人名称
     *
     * @return
     */
    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    /**
     * 获得指派人机构ID
     *
     * @return
     */
    public String getAssigneeOrgId() {
        return assigneeOrgId;
    }

    public void setAssigneeOrgId(String assigneeOrgId) {
        this.assigneeOrgId = assigneeOrgId;
    }

    /**
     * 获得指派人机构名称
     *
     * @return
     */
    public String getAssigneeOrgName() {
        return assigneeOrgName;
    }

    public void setAssigneeOrgName(String assigneeOrgName) {
        this.assigneeOrgName = assigneeOrgName;
    }

    /**
     * 获得流程实例创建日期
     *
     * @return
     */
    public Date getProcessInstanceCreateTime() {
        return processInstanceCreateTime;
    }

    public void setProcessInstanceCreateTime(Date processInstanceCreateTime) {
        this.processInstanceCreateTime = processInstanceCreateTime;
    }

    /**
     * 获得创建日期
     *
     * @return
     */
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获得结束时间
     *
     * @return
     */
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获得截止完成时间
     *
     * @return
     */
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * 获得优先级
     *
     * @return
     */
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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
     * 获得审批意见
     *
     * @return
     */
    public String getApprovalMemo() {
        return approvalMemo;
    }

    public void setApprovalMemo(String approvalMemo) {
        this.approvalMemo = approvalMemo;
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
     * 获得状态
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    /**
     * 获得候选人列表
     *
     * @return
     */
    public List<Map<String, Object>> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(List<Map<String, Object>> candidateList) {
        this.candidateList = candidateList;
    }

    /**
     * 获得子流程
     *
     * @return
     */
    public List<Task> getChildren() {
        return children;
    }

    public void setChildren(List<Task> children) {
        this.children = children;
    }
}
