package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 人员组织机构
 *
 * @author: TangLiang
 * @date: 2022/02/08 09:50
 * @since: 1.0
 */
public interface PersonToDeptService {

    /**
     * 查询人员组织机构
     *
     * @param V_PERCODE  人员编码
     * @param V_ORGCODE  厂矿编码
     * @param V_DEPTCODE 机构编码
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectPersonToDept(String V_PERCODE, String V_ORGCODE, String V_DEPTCODE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_PERCODE  人员编码
     * @param V_ORGCODE  厂矿编码
     * @param V_DEPTCODE 机构编码
     */
    int countPersonToDept(String V_PERCODE, String V_ORGCODE, String V_DEPTCODE);

    /**
     * 新增人员组织机构
     *
     * @param V_PERCODELIST 人员编码集合
     * @param V_ORGCODE     厂矿编码
     * @param V_DEPTCODE    机构编码
     * @param V_PER_EDIT    最后修改人
     */
    int insertPersonToDeptBatch(List<String> V_PERCODELIST, String V_ORGCODE, String V_DEPTCODE, String V_PER_EDIT);

    /**
     * 删除人员组织机构
     *
     * @param I_ID id
     */
    int deletePersonToDept(String I_ID);

}