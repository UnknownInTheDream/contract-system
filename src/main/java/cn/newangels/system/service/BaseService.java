package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 人员机构基础信息
 *
 * @author: ll
 * @since: 1.0
 */
public interface BaseService {

    /**
     * 根据人员编码获取人员流程相关信息
     *
     * @param V_PERCODE_FORM
     */
    Map<String, Object> getOperator(String V_PERCODE_FORM);

    /**
     * 加载人员机构岗位
     *
     * @param V_PERCODE
     */
    Map<String, Object> loadEmpByEmpCode(String V_PERCODE);

    /**
     * 加载人员机构岗位权限
     *
     * @param V_PERCODE
     */
    Map<String, Object> loadEmpInfoByEmpCode(String V_PERCODE);

    /**
     * 查询人员
     *
     * @param V_ROLECODE 角色编码
     * @param V_DEPTCODE
     */
    List<Map<String, Object>> selectEmpByDeptRole(String V_DEPTCODE, String V_ROLECODE);

    /**
     * 查询角色
     *
     * @param V_PERCODE
     */
    List<Map<String, Object>> selectRoleByEmpCode(String V_PERCODE);
}