package cn.newangels.system.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UserTaskDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskName;
    private String taskDefinitionKey;
    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private String businessKey;
    private String startUser;
    private String startUserName;
    private String assignee;
    private String assigneeName;
    private String approvalMemo;
    private String tenantId;
    private Map<String, Object> processVariables;
    private List<Map<String, Object>> candidateList;

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
     * 获得任务实例ID
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
     * 获得任务定义ID
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
     * 获得任务定义名称
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
     * 获得任务定义KEY
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
     * 获得启动人员
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
     * 获得启动人员名称
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
}
