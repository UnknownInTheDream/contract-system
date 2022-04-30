package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 人员岗位
 *
 * @author: TangLiang
 * @date: 2022/02/07 14:39
 * @since: 1.0
 */
public interface PersonToPostService {

    /**
     * 查询人员岗位
     *
     * @param V_PERCODE  人员编码
     * @param V_POSTCODE 岗位编码
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectPersonToPost(String V_PERCODE, String V_POSTCODE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_PERCODE  人员编码
     * @param V_POSTCODE 岗位编码
     */
    int countPersonToPost(String V_PERCODE, String V_POSTCODE);

    /**
     * 新增人员岗位
     *
     * @param V_PERCODE      人员编码
     * @param V_POSTCODELIST 岗位编码集合
     * @param V_PER_EDIT     最后修改人
     */
    int insertPersonToPostBatch(String V_PERCODE, List<String> V_POSTCODELIST, String V_PER_EDIT);

    /**
     * 删除人员岗位
     *
     * @param I_ID i_id
     */
    int deletePersonToPost(String I_ID);

}