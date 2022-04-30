package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author: JinHongKe
 * @date: 2022/2/10
 * @since: 1.0
 */
public interface RoleService {

    /**
     * 加载角色
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadRole(String I_ID);

    /**
     * 查询角色
     *
     * @param V_ORLECODE 角色编码
     * @param V_ORLENAME 角色名称
     * @param V_STATUS   1启用 0 不启用
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectRole(String V_ORLECODE, String V_ORLENAME, String V_STATUS, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_ORLECODE 角色编码
     * @param V_ORLENAME 角色名称
     * @param V_STATUS   1启用 0 不启用
     */
    int countRole(String V_ORLECODE, String V_ORLENAME, String V_STATUS);

    /**
     * 新增角色
     *
     * @param I_ID       i_id
     * @param V_ORLECODE 人员编码
     * @param V_ORLENAME 角色名称
     * @param V_PER_EDIT 最后修改人
     */
    int insertRole(String I_ID, String V_ORLECODE, String V_ORLENAME, String V_PER_EDIT);

    /**
     * 修改角色
     *
     * @param I_ID       id
     * @param V_ORLENAME 角色名称
     * @param V_PER_EDIT 最后修改人
     */
    int updateRole(String I_ID, String V_ORLENAME, String V_PER_EDIT);

    /**
     * 修改角色状态
     *
     * @param I_ID       id
     * @param V_STATUS   状态1/0
     * @param V_PER_EDIT 最后修改人
     */
    int updateRoleStatus(String I_ID, String V_STATUS, String V_PER_EDIT);


    /**
     * 删除角色
     *
     * @param I_ID i_id
     */
    int deleteRole(String I_ID);
}
