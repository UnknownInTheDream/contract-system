package cn.newangels.system.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ProcessInstance implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_RUNNING = "运行中";
    public static final String STATUS_FINISHED = "已完结";
    public static final String STATUS_SUSPENDED = "挂起";
    public static final String STATUS_DRAFT = "草稿";

    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private String businessKey;
    private String startActivityId;
    private String superProcessInstanceId;
    private String startUser;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String deleteReason;
    private String status;
    private Map<String, Object> processVariables;

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
     * 获得业务KEY
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
     * 获得开始节点ID
     *
     * @return
     */
    public String getStartActivityId() {
        return startActivityId;
    }

    public void setStartActivityId(String startActivityId) {
        this.startActivityId = startActivityId;
    }

    /**
     * 获得父流程ID
     *
     * @return
     */
    public String getSuperProcessInstanceId() {
        return superProcessInstanceId;
    }

    public void setSuperProcessInstanceId(String superProcessInstanceId) {
        this.superProcessInstanceId = superProcessInstanceId;
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
     * 获得启动时间
     *
     * @return
     */
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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
     * 获得流转时间
     *
     * @return
     */
    public Long getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(Long durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    /**
     * 获得删除原因
     *
     * @return
     */
    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
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
}
