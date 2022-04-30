package cn.newangels.system.dto;

import java.util.Map;

public class ProcessVariables {
    private Map<String, Object> processVariables;

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
