package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 人员专有菜单
 *
 * @author: ll
 * @date: 2022/02/11 14:54
 * @since: 1.0
 */
public interface PersonToMenuService {

    /**
     * 查询人员专有菜单
     *
     * @param V_PERCODE 人员编码
     * @param current   当前页数
     * @param pageSize  每次显示数量
     */
    List<Map<String, Object>> selectPersonToMenu(String V_PERCODE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_PERCODE 人员编码
     */
    int countPersonToMenu(String V_PERCODE);

    /**
     * 新增人员专有菜单
     *
     * @param V_PERCODE      人员编码
     * @param V_MENUCODELIST 菜单编码id
     * @param V_PER_EDIT     最后修改人
     */
    int insertPersonToMenuBatch(String V_PERCODE, List<String> V_MENUCODELIST, String V_PER_EDIT);

    /**
     * 删除人员专有菜单
     *
     * @param I_ID id
     */
    int deletePersonToMenu(String I_ID);

}