package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 审批常用语
 *
 * @author: TangLiang
 * @date: 2022/03/07 15:02
 * @since: 1.0
 */
public interface ApprovalOpinionsService {

    /**
     * 加载审批常用语
     *
     * @param I_ID id
     */
    Map<String, Object> loadApprovalOpinions(String I_ID);

    /**
     * 查询审批常用语
     *
     * @param V_PERCODE  人员编码
     * @param V_OPINIONS 审批意见
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectApprovalOpinions(String V_PERCODE, String V_OPINIONS, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_PERCODE  人员编码
     * @param V_OPINIONS 审批意见
     */
    int countApprovalOpinions(String V_PERCODE, String V_OPINIONS);

    /**
     * 新增审批常用语
     *
     * @param V_PERCODE  人员编码
     * @param V_OPINIONS 审批意见
     */
    int insertApprovalOpinions(String V_PERCODE, String V_OPINIONS);

    /**
     * 修改审批常用语
     *
     * @param I_ID       id
     * @param V_OPINIONS 审批意见
     */
    int updateApprovalOpinions(String I_ID, String V_OPINIONS);

    /**
     * 删除审批常用语
     *
     * @param I_ID id
     */
    int deleteApprovalOpinions(String I_ID);

}