package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 专业
 *
 * @author: ll
 * @date: 2022/02/11 10:31
 * @since: 1.0
 */
public interface MajorService {

    /**
     * 查询专业
     *
     * @param V_DEPTCODE 主管该专业的业务部门
     * @param current    当前页数
     * @param pageSize   每次显示数量
     */
    List<Map<String, Object>> selectMajor(String V_DEPTCODE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_DEPTCODE 主管该专业的业务部门
     */
    int countMajor(String V_DEPTCODE);

    /**
     * 新增专业
     *
     * @param V_NAMELIST 专业名称集合
     * @param V_DEPTCODE 主管该专业的业务部门
     * @param V_PER_EDIT 最后修改人
     */
    int insertMajorBatch(List<String> V_NAMELIST, String V_DEPTCODE, String V_PER_EDIT);

    /**
     * 删除专业
     *
     * @param I_ID id
     */
    int deleteMajor(String I_ID);

}