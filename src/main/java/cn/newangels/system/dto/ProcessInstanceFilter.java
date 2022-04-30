package cn.newangels.system.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ProcessInstanceFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Integer TRUE = 1;
    public static final Integer FALSE = 0;

    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String businessKey;
    private String startUser;
    private String involvedUserId;
    private Date startedBefore;
    private Date startedAfter;
    private Date finishedBefore;
    private Date finishedAfter;
    private Integer finished;
    private Integer suspended;
    private Map<String, Object> processVariables;

    public ProcessInstanceFilter() {
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
     * 获得参与人员
     *
     * @return
     */
    public String getInvolvedUserId() {
        return involvedUserId;
    }

    public void setInvolvedUserId(String involvedUserId) {
        this.involvedUserId = involvedUserId;
    }

    /**
     * 获得查询条件,启动早于时间
     *
     * @return
     */
    public Date getStartedBefore() {
        return startedBefore;
    }

    public void setStartedBefore(Date startedBefore) {
        this.startedBefore = startedBefore;
    }

    /**
     * 获得查询条件,启动晚于时间
     *
     * @return
     */
    public Date getStartedAfter() {
        return startedAfter;
    }

    public void setStartedAfter(Date startedAfter) {
        this.startedAfter = startedAfter;
    }

    /**
     * 获得查询条件,结束早于时间
     *
     * @return
     */
    public Date getFinishedBefore() {
        return finishedBefore;
    }

    public void setFinishedBefore(Date finishedBefore) {
        this.finishedBefore = finishedBefore;
    }

    /**
     * 获得查询条件,结束晚于时间
     *
     * @return
     */
    public Date getFinishedAfter() {
        return finishedAfter;
    }

    public void setFinishedAfter(Date finishedAfter) {
        this.finishedAfter = finishedAfter;
    }

    /**
     * 获得是否已经结束
     *
     * @return
     */
    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    /**
     * 获得是否挂起
     *
     * @return
     */
    public Integer getSuspended() {
        return suspended;
    }

    public void setSuspended(Integer suspended) {
        this.suspended = suspended;
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
