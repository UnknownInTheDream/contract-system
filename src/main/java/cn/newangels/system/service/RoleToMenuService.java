package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 角色对应菜单
 *
 * @author: JinHongKe
 * @date: 2022/2/10
 * @since: 1.0
 */
public interface RoleToMenuService {

    /**
     * 查询角色对应菜单
     *
     * @param V_ORLECODE 角色编码
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectRoleToMenu(String V_ORLECODE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_ORLECODE 角色编码
     */
    int countRoleToMenu(String V_ORLECODE);

    /**
     * 新增角色对应菜单
     *
     * @param V_ORLECODE     角色编码
     * @param V_MENUCODELIST 菜单编码集合
     * @param V_PER_EDIT     最后修改人
     */
    int insertRoleToMenuBatch(String V_ORLECODE, List<String> V_MENUCODELIST, String V_PER_EDIT);

    /**
     * 删除角色对应菜单
     *
     * @param I_ID i_id
     */
    int deleteRoleToMenu(String I_ID);

}
