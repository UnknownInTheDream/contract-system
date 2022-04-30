package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 系统菜单
 *
 * @author: TangLiang
 * @date: 2022/01/26 09:16
 * @since: 1.0
 */
public interface MenuService {

    /**
     * 加载系统菜单
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadMenu(String I_ID);

    /**
     * 查询系统菜单
     *
     * @param I_PID     上级菜单id
     * @param V_CODE    菜单编码
     * @param V_NAME    菜单名称
     * @param V_SYSTYPE 系统编码
     */
    List<Map<String, Object>> selectMenu(String I_PID, String V_CODE, String V_NAME, String V_SYSTYPE);

    /**
     * @param V_PERCODE 人员编码
     * @param V_SYSTYPE 系统编码
     */
    List<Map<String, Object>> selectPersonMenu(String V_PERCODE, String V_SYSTYPE);

    /**
     * 新增系统菜单
     *
     * @param I_ID          i_id
     * @param V_NAME        菜单名称
     * @param I_PID         上级菜单,根节点值为-1
     * @param V_ADDRESS     地址
     * @param V_ADDRESS_ICO 图标地址
     * @param V_SYSTYPE     系统编码
     * @param I_ORDER       排序
     * @param V_PER_EDIT    最后修改人
     */
    int insertMenu(String I_ID, String V_NAME, String I_PID, String V_ADDRESS, String V_ADDRESS_ICO, String V_SYSTYPE, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 修改系统菜单
     *
     * @param I_ID          i_id
     * @param V_NAME        菜单名称
     * @param V_ADDRESS     地址
     * @param V_ADDRESS_ICO 图标地址
     * @param V_SYSTYPE     系统编码
     * @param I_ORDER       排序
     * @param V_PER_EDIT    最后修改人
     */
    int updateMenu(String I_ID, String V_NAME, String V_ADDRESS, String V_ADDRESS_ICO, String V_SYSTYPE, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 删除系统菜单
     *
     * @param I_ID i_id
     */
    int deleteMenu(String I_ID);

}