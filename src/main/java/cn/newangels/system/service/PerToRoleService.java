package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 人员角色
 *
 * @author: TangLiang
 * @date: 2022/2/10 10:18
 * @since: 1.0
 */
public interface PerToRoleService {

    /**
     * 查询人员角色
     *
     * @param V_PERCODE  人员编码
     * @param V_ORLECODE 角色编码
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectPerToRole(String V_PERCODE, String V_ORLECODE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_PERCODE  人员编码
     * @param V_ORLECODE 角色编码
     */
    int countPerToRole(String V_PERCODE, String V_ORLECODE);

    /**
     * 新增人员角色
     *
     * @param V_PERCODE      人员编码
     * @param V_ORLECODELIST 角色编码集合
     * @param V_PER_EDIT     最后修改人
     */
    int insertPerToRoleBatch(String V_PERCODE, List<String> V_ORLECODELIST, String V_PER_EDIT);

    /**
     * 删除人员角色
     *
     * @param I_ID i_id
     */
    int deletePerToRole(String I_ID);
}
