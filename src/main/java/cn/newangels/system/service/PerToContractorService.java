package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 承揽方收藏
 *
 * @author: JinHongKe
 * @date: 2022/01/24
 * @since: 1.0
 */
public interface PerToContractorService {

    /**
     * 查询个人承揽方收藏
     *
     * @param V_NAME        承揽方名称
     * @param V_PERCODE     人员编码
     * @param CONTRACTOR_ID 承揽方id
     * @param current       当前页数
     * @param pageSize      每页显示数量
     */
    List<Map<String, Object>> selectPerToContractor(String V_NAME, String V_PERCODE, String CONTRACTOR_ID, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_NAME        承揽方名称
     * @param V_PERCODE     人员编码
     * @param CONTRACTOR_ID 承揽方id
     */
    int countPerToContractor(String V_NAME, String V_PERCODE, String CONTRACTOR_ID);

    /**
     * 新增个人承揽方收藏
     *
     * @param CONTRACTOR_IDLIST 个人承揽方id集合
     * @param V_PERCODE         人员编码
     * @param V_PER_EDIT        最后修改人
     */
    int insertPerToContractorBatch(List<String> CONTRACTOR_IDLIST, String V_PERCODE, String V_PER_EDIT);

    /**
     * 删除个人承揽方收藏
     *
     * @param I_IDLIST 个人承揽方id集合
     */
    int deletePerToContractorBatch(List<String> I_IDLIST);
}
