package cn.newangels.system.dto;

import java.io.Serializable;

/**
 * ActivitiVariable
 *
 * @author mwd 2021-04-21
 */
public class ActivitiVariable implements Serializable {

    private String name;

    private Object value;

    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
