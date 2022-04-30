package cn.newangels.system.dto;

import org.activiti.engine.delegate.VariableScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * MyVariableScope
 *
 * @author mwd 2021-04-21
 */
public class MyVariableScope implements VariableScope {
    private Map<String, Object> variables = null;

    public MyVariableScope(Map<String, Object> variables) {
        this.variables = variables == null ? new HashMap<>() : variables;
    }

    @Override
    public Map<String, Object> getVariables() {
        return null;
    }

    @Override
    public Map<String, Object> getVariables(Collection<String> collection) {
        return null;
    }

    @Override
    public Map<String, Object> getVariables(Collection<String> collection, boolean b) {
        return null;
    }

    @Override
    public Map<String, Object> getVariablesLocal() {
        return null;
    }

    @Override
    public Map<String, Object> getVariablesLocal(Collection<String> collection) {
        return null;
    }

    @Override
    public Map<String, Object> getVariablesLocal(Collection<String> collection, boolean b) {
        return null;
    }

    @Override
    public Object getVariable(String s) {
        return null;
    }

    @Override
    public Object getVariable(String s, boolean b) {
        return null;
    }

    @Override
    public Object getVariableLocal(String s) {
        return null;
    }

    @Override
    public Object getVariableLocal(String s, boolean b) {
        return null;
    }

    @Override
    public <T> T getVariable(String s, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T getVariableLocal(String s, Class<T> aClass) {
        return null;
    }

    @Override
    public Set<String> getVariableNames() {
        return null;
    }

    @Override
    public Set<String> getVariableNamesLocal() {
        return null;
    }

    @Override
    public void setVariable(String s, Object o) {

    }

    @Override
    public void setVariable(String s, Object o, boolean b) {

    }

    @Override
    public Object setVariableLocal(String s, Object o) {
        return null;
    }

    @Override
    public Object setVariableLocal(String s, Object o, boolean b) {
        return null;
    }

    @Override
    public void setVariables(Map<String, ?> map) {

    }

    @Override
    public void setVariablesLocal(Map<String, ?> map) {

    }

    @Override
    public boolean hasVariables() {
        return false;
    }

    @Override
    public boolean hasVariablesLocal() {
        return false;
    }

    @Override
    public boolean hasVariable(String s) {
        return false;
    }

    @Override
    public boolean hasVariableLocal(String s) {
        return false;
    }

    @Override
    public void createVariableLocal(String s, Object o) {

    }

    @Override
    public void removeVariable(String s) {

    }

    @Override
    public void removeVariableLocal(String s) {

    }

    @Override
    public void removeVariables(Collection<String> collection) {

    }

    @Override
    public void removeVariablesLocal(Collection<String> collection) {

    }

    @Override
    public void removeVariables() {

    }

    @Override
    public void removeVariablesLocal() {

    }
    //.....以下都是VariableScope 的默认实现方法，没有具体代码，就不贴了
}
