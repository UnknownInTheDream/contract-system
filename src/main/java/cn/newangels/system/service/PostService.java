package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 岗位
 *
 * @author: TangLiang
 * @date: 2022/01/28 11:09
 * @since: 1.0
 */
public interface PostService {

    /**
     * 加载岗位
     *
     * @param I_ID id
     */
    Map<String, Object> loadPost(String I_ID);

    /**
     * 查询岗位
     *
     * @param V_POSTCODE    岗位编码
     * @param V_POSTNAME    岗位名称
     * @param V_POSTCODE_UP 上级岗位
     * @param V_ISADMIN     管理员设置 1为管理员 0不是
     * @param current       当前页数
     * @param pageSize      每次显示数量
     */
    List<Map<String, Object>> selectPost(String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, String V_ISADMIN, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_POSTCODE    岗位编码
     * @param V_POSTNAME    岗位名称
     * @param V_POSTCODE_UP 上级岗位
     * @param V_ISADMIN     管理员设置 1为管理员 0不是
     */
    int countPost(String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, String V_ISADMIN);

    /**
     * 新增岗位
     *
     * @param I_ID          id
     * @param V_POSTCODE    岗位编码
     * @param V_POSTNAME    岗位名称
     * @param V_POSTCODE_UP 上级岗位
     * @param I_ORDER       排序
     * @param V_ISADMIN     管理员设置 1为管理员 0不是
     * @param V_PER_EDIT    最后修改人
     */
    int insertPost(String I_ID, String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, Integer I_ORDER, String V_ISADMIN, String V_PER_EDIT);

    /**
     * 修改岗位
     *
     * @param I_ID       id
     * @param V_POSTCODE 岗位编码
     * @param V_POSTNAME 岗位名称
     * @param I_ORDER    排序
     * @param V_ISADMIN  管理员设置 1为管理员 0不是
     * @param V_PER_EDIT 最后修改人
     */
    int updatePost(String I_ID, String V_POSTCODE, String V_POSTNAME, Integer I_ORDER, String V_ISADMIN, String V_PER_EDIT);

    /**
     * 修改岗位状态
     *
     * @param I_ID       id
     * @param V_STATUS   状态1/0
     * @param V_PER_EDIT 最后修改人
     */
    int updatePostStatus(String I_ID, String V_STATUS, String V_PER_EDIT);

    /**
     * 删除岗位
     *
     * @param I_ID id
     */
    int deletePost(String I_ID);

}