package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 定作方
 *
 * @author: JinHongKe
 * @date: 2022/01/24
 * @since: 1.0
 */
public interface SponsorService {

    /**
     * 加载定作方
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadSponsor(String I_ID);

    /**
     * 查询定制方
     *
     * @param V_SPONSORCODE 定作方编码
     * @param V_SPONSORNAME 定作方名称
     * @param V_STATUS      状态
     * @param current       当前页数
     * @param pageSize      每次显示数量
     */
    List<Map<String, Object>> selectSponsor(String V_SPONSORCODE, String V_SPONSORNAME, String V_STATUS, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_SPONSORCODE 定作方编码
     * @param V_SPONSORNAME 定作方名称
     * @param V_STATUS      状态
     */
    int countSponsor(String V_SPONSORCODE, String V_SPONSORNAME, String V_STATUS);

    /**
     * 新增定作方
     *
     * @param I_ID          i_id
     * @param V_SPONSORCODE 定作方编码
     * @param V_SPONSORNAME 定作方名称
     * @param V_SIMPLENAME  定作方简称
     * @param V_OFFICER     定作方法人
     * @param V_PHONE       电话号码
     * @param I_ORDER       显示顺序
     * @param V_PER_EDIT    最后修改人
     */
    int insertSponsor(String I_ID, String V_SPONSORCODE, String V_SPONSORNAME, String V_SIMPLENAME, String V_OFFICER, String V_PHONE, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 修改定作方
     *
     * @param I_ID          i_id
     * @param V_SPONSORCODE 定作方编码
     * @param V_SPONSORNAME 定作方名称
     * @param V_SIMPLENAME  定作方简称
     * @param V_OFFICER     定作方法人
     * @param V_PHONE       电话号码
     * @param I_ORDER       显示顺序
     * @param V_PER_EDIT    最后修改人
     */
    int updateSponsor(String I_ID, String V_SPONSORCODE, String V_SPONSORNAME, String V_SIMPLENAME, String V_OFFICER, String V_PHONE, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 修改定作方状态
     *
     * @param I_ID       i_id
     * @param V_STATUS   状态1/0
     * @param V_PER_EDIT 最后修改人
     */
    int updateSponsorStatus(String I_ID, String V_STATUS, String V_PER_EDIT);

    /**
     * 删除定作方
     *
     * @param I_ID i_id
     */
    int deleteSponsor(String I_ID);
}
