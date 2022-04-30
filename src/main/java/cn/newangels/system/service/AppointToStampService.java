package cn.newangels.system.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约签章表
 *
 * @author: ll
 * @date: 2022/03/23 15:18
 * @since: 1.0
 */
public interface AppointToStampService {

    /**
     * 加载预约签章表
     *
     * @param I_ID id
     */
    Map<String, Object> loadAppointToStamp(String I_ID);

    /**
     * 查询预约签章表
     *
     * @param V_BEGIN_DATE  预约起始时间
     * @param V_END_DATE    预约终止时间
     * @param V_STAMPSTATUS 状态（1 预约申请中 2 已预约确认 3已盖章 4驳回）
     * @param V_PER_EDIT    最后修改人
     * @param flag          状态
     * @param current       当前页数
     * @param pageSize      每次显示数量
     */
    List<Map<String, Object>> selectAppointToStamp(Date V_BEGIN_DATE, Date V_END_DATE, Integer V_STAMPSTATUS, String V_PER_EDIT, Boolean flag, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_BEGIN_DATE  预约起始时间
     * @param V_END_DATE    预约终止时间
     * @param V_STAMPSTATUS 状态（1 预约申请中 2 已预约确认 3已盖章 4驳回）
     * @param V_PER_EDIT    最后修改人
     * @param flag          状态
     */
    int countAppointToStamp(Date V_BEGIN_DATE, Date V_END_DATE, Integer V_STAMPSTATUS, String V_PER_EDIT, Boolean flag);

    /**
     * 新增预约签章表
     *
     * @param V_CONTRACTIDLIST 合同ID
     * @param V_APPOINTDATE    预约时间
     * @param V_APPLICANT      预约人
     * @param V_PER_EDIT       最后修改人
     */
    int insertAppointToStampBatch(List<String> V_CONTRACTIDLIST, String V_APPOINTDATE, String V_APPLICANT, String V_PER_EDIT);

    /**
     * 修改预约签章表
     *
     * @param I_ID          id
     * @param V_APPOINTDATE 预约时间
     * @param V_STAMPSTATUS 状态（1 预约申请中 2 已预约确认 3已盖章 4驳回）
     */
    int updateAppointToStamp(String I_ID, String V_APPOINTDATE, Integer V_STAMPSTATUS);

    /**
     * 修改预约签章表
     *
     * @param I_IDLIST      合同ID
     * @param V_APPOINTDATE 预约时间
     * @param V_STAMPSTATUS 状态（1 预约申请中 2 已预约确认 3已盖章 4驳回）
     */
    int updateAppointToStampBatch(List<String> I_IDLIST, String V_APPOINTDATE, Integer V_STAMPSTATUS);

    /**
     * 修改预约签章状态表
     *
     * @param I_IDLIST      合同ID
     * @param V_STAMPSTATUS 状态（1 预约申请中 2 已预约确认 3已盖章 4驳回）
     */
    int updateStampStatusBatch(List<String> I_IDLIST, Integer V_STAMPSTATUS, List<String> V_CONTRACTIDLIST);

    /**
     * 删除预约签章表
     *
     * @param I_ID id
     */
    int deleteAppointToStamp(String I_ID);

}