package cn.newangels.system.dto;

import java.io.Serializable;

public class ProcessDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private String category;
    private Integer version;
    private Integer suspensionState;
    private String deploymentId;
    private Integer totalProcessInstanceCount;
    private Integer activeProcessInstanceCount;
    private Integer activeTaskCount;
    private Integer overtimeTaskCount;

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
     * 获得分类
     *
     * @return
     */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获得版本
     *
     * @return
     */
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
     * 获得部署ID
     *
     * @return
     */
    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    /**
     * 获得流程实例数量统计
     *
     * @return
     */
    public Integer getTotalProcessInstanceCount() {
        return totalProcessInstanceCount;
    }

    public void setTotalProcessInstanceCount(Integer totalProcessInstanceCount) {
        this.totalProcessInstanceCount = totalProcessInstanceCount;
    }

    /**
     * 获得活动的流程实例数量统计
     *
     * @return
     */
    public Integer getActiveProcessInstanceCount() {
        return activeProcessInstanceCount;
    }

    public void setActiveProcessInstanceCount(Integer activeProcessInstanceCount) {
        this.activeProcessInstanceCount = activeProcessInstanceCount;
    }

    /**
     * 获得活动的任务数量统计
     *
     * @return
     */
    public Integer getActiveTaskCount() {
        return activeTaskCount;
    }

    public void setActiveTaskCount(Integer activeTaskCount) {
        this.activeTaskCount = activeTaskCount;
    }

    /**
     * 获得超时任务数量统计
     *
     * @return
     */
    public Integer getOvertimeTaskCount() {
        return overtimeTaskCount;
    }

    public void setOvertimeTaskCount(Integer overtimeTaskCount) {
        this.overtimeTaskCount = overtimeTaskCount;
    }
}