package cn.newangels.system.base;

import java.io.Serializable;

public class BaseOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String propertyName;
    private boolean asc = true;

    /**
     * 获得排序字段名称
     *
     * @return
     */
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * 获得是否为升序
     *
     * @return
     */
    public boolean isAsc() {
        return asc;
    }

    public boolean getAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
