package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 专业流程关系
 *
 * @author: TangLiang
 * @date: 2022/03/02 16:59
 * @since: 1.0
 */
public interface MajorToFlowService {

    /**
     * 查询专业流程关系
     *
     * @param V_MAJORID 专业id
     * @param V_CONTYPE 合同类型(关联协议,中标,议标)
     * @param V_FLOWID  流程id
     * @param current   当前页数
     * @param pageSize  每次显示数量
     */
    List<Map<String, Object>> selectMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_MAJORID 专业id
     * @param V_CONTYPE 合同类型(关联协议,中标,议标)
     * @param V_FLOWID  流程id
     */
    int countMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID);

    /**
     * 新增专业流程关系
     *
     * @param V_MAJORID 专业id
     * @param V_CONTYPE 合同类型(关联协议,中标,议标)
     * @param V_FLOWID  流程id
     */
    int insertMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID);

    /**
     * 删除专业流程关系
     *
     * @param I_ID id
     */
    int deleteMajorToFlow(String I_ID);

    /**
     * 删除专业流程关系表
     */
    int deleteMajorFlow(String V_MAJORID, String V_CONTYPE);

}