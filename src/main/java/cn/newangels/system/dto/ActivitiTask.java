package cn.newangels.system.dto;

import java.io.Serializable;

/**
 * ActivitiTask
 *
 * @author mwd 2021-04-21
 */
public class ActivitiTask implements Serializable {

    private String id;

    private AssigneeEntity assignee;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AssigneeEntity getAssignee() {
        return assignee;
    }

    public void setAssignee(AssigneeEntity assignee) {
        this.assignee = assignee;
    }
}
