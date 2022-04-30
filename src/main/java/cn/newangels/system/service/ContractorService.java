package cn.newangels.system.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 承揽方
 *
 * @author: JinHongKe
 * @date: 2022/01/24
 * @since: 1.0
 */
public interface ContractorService {

    /**
     * 加载承揽方
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadContractor(String I_ID);

    /**
     * 查询承揽方
     *
     * @param V_NAME   承揽方名称
     * @param V_STATUS 状态
     * @param current  当前页数
     * @param pageSize 每页显示数量
     */
    List<Map<String, Object>> selectContractor(String V_NAME, String V_STATUS, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_NAME   承揽方名称
     * @param V_STATUS 状态
     */
    int countContractor(String V_NAME, String V_STATUS);

    /**
     * 新增承揽方
     *
     * @param I_ID             i_id
     * @param V_NAME           承揽方名称
     * @param V_ADDRESS        地址
     * @param V_LEGAL          法定代表人
     * @param V_REPRESENTITIVE 委托代理人
     * @param V_PHONE          联系电话
     * @param V_BANK           开户银行
     * @param V_ACCOUNT        银行帐号
     * @param V_NATURE         企业性质
     * @param I_REGEREDCAPITAL 注册资金(元)
     * @param V_LICENSE        经营许可
     * @param V_CREDIT         信用等级
     * @param V_PER_EDIT       最后修改人
     */
    int insertContractor(String I_ID, String V_NAME, String V_ADDRESS, String V_LEGAL, String V_REPRESENTITIVE, String V_PHONE, String V_BANK, String V_ACCOUNT, String V_NATURE, BigDecimal I_REGEREDCAPITAL, String V_LICENSE, String V_CREDIT, String V_PER_EDIT);

    /**
     * 修改承揽方
     *
     * @param I_ID             i_id
     * @param V_NAME           承揽方名称
     * @param V_ADDRESS        地址
     * @param V_LEGAL          法定代表人
     * @param V_REPRESENTITIVE 委托代理人
     * @param V_PHONE          联系电话
     * @param V_BANK           开户银行
     * @param V_ACCOUNT        银行帐号
     * @param V_NATURE         企业性质
     * @param I_REGEREDCAPITAL 注册资金(元)
     * @param V_LICENSE        经营许可
     * @param V_CREDIT         信用等级
     * @param V_PER_EDIT       最后修改人
     */
    int updateContractor(String I_ID, String V_NAME, String V_ADDRESS, String V_LEGAL, String V_REPRESENTITIVE, String V_PHONE, String V_BANK, String V_ACCOUNT, String V_NATURE, BigDecimal I_REGEREDCAPITAL, String V_LICENSE, String V_CREDIT, String V_PER_EDIT);

    /**
     * 修改承揽方状态
     *
     * @param I_ID       id
     * @param V_STATUS   状态1/0
     * @param V_PER_EDIT 最后修改人
     */
    int updateContractorStatus(String I_ID, String V_STATUS, String V_PER_EDIT);

    /**
     * 删除承揽方
     *
     * @param I_ID i_id
     */
    int deleteContractor(String I_ID);
}
