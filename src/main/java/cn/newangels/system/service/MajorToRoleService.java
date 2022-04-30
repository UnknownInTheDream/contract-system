package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 专业对应角色
 *
 * @author: TangLiang
 * @date: 2022/02/17 16:48
 * @since: 1.0
 */
public interface MajorToRoleService {

    /**
     * 查询专业对应角色
     *
     * @param V_ORLECODE 角色编码
     * @param V_MAJORID  专业id
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectMajorToRole(String V_ORLECODE, String V_MAJORID, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_ORLECODE 角色编码
     * @param V_MAJORID  专业id
     */
    int countMajorToRole(String V_ORLECODE, String V_MAJORID);

    /**
     * 新增专业对应角色
     *
     * @param V_ORLECODELIST 角色编码集合
     * @param V_MAJORID      专业id
     * @param V_PER_EDIT     最后修改人
     */
    int insertMajorToRoleBatch(List<String> V_ORLECODELIST, String V_MAJORID, String V_PER_EDIT);

    /**
     * 删除专业对应角色
     *
     * @param I_ID id
     */
    int deleteMajorToRole(String I_ID);

}