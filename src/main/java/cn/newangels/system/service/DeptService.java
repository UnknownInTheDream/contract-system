package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 组织机构
 *
 * @author: TangLiang
 * @date: 2022/01/27 09:21
 * @since: 1.0
 */
public interface DeptService {

    /**
     * 加载组织机构
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadDept(String I_ID);

    /**
     * 查询组织机构
     *
     * @param V_DEPTCODE    机构编码
     * @param V_DEPTNAME    机构名称（简）
     * @param V_DEPTTYPE    机构类型（公司部门，厂矿，厂矿部门，厂矿车间）
     * @param V_DEPTCODE_UP 上级机构编码
     * @param V_STATUS      状态 1/0
     * @param current       当前页数
     * @param pageSize      每次显示数量
     */
    List<Map<String, Object>> selectDept(String V_DEPTCODE, String V_DEPTNAME, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_STATUS, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_DEPTCODE    机构编码
     * @param V_DEPTNAME    机构名称（简）
     * @param V_DEPTTYPE    机构类型（公司部门，厂矿，厂矿部门，厂矿车间）
     * @param V_DEPTCODE_UP 上级机构编码
     * @param V_STATUS      状态 1/0
     */
    int countDept(String V_DEPTCODE, String V_DEPTNAME, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_STATUS);

    /**
     * 新增组织机构
     *
     * @param I_ID            i_id
     * @param V_DEPTCODE      机构编码
     * @param V_DEPTNAME      机构名称（简）
     * @param V_DEPTNAME_FULL 机构名称（全）
     * @param V_DEPTTYPE      机构类型（公司部门，厂矿，厂矿部门，厂矿车间）
     * @param V_DEPTCODE_UP   上级机构编码（-1为顶级）
     * @param V_DLFR          单位性质(如独立法人)
     * @param V_PRODUCE       是否主体生产单位
     * @param V_DQ            地区(鞍山，弓长岭)
     * @param V_PER_EDIT      最后修改人
     */
    int insertDept(String I_ID, String V_DEPTCODE, String V_DEPTNAME, String V_DEPTNAME_FULL, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_DLFR, String V_PRODUCE, String V_DQ, String V_PER_EDIT);

    /**
     * 修改组织机构
     *
     * @param I_ID            i_id
     * @param V_DEPTCODE      机构编码
     * @param V_DEPTNAME      机构名称（简）
     * @param V_DEPTNAME_FULL 机构名称（全）
     * @param V_DEPTTYPE      机构类型（公司部门，厂矿，厂矿部门，厂矿车间）
     * @param V_DLFR          单位性质(如独立法人)
     * @param V_PRODUCE       是否主体生产单位
     * @param V_DQ            地区(鞍山，弓长岭)
     * @param V_PER_EDIT      最后修改人
     */
    int updateDept(String I_ID, String V_DEPTCODE, String V_DEPTNAME, String V_DEPTNAME_FULL, String V_DEPTTYPE, String V_DLFR, String V_PRODUCE, String V_DQ, String V_PER_EDIT);

    /**
     * 修改组织机构状态
     *
     * @param I_ID      i_id
     * @param V_STATUS  状态 1/0
     * @param V_PERCODE 修改人
     */
    int updateDeptStatus(String I_ID, String V_STATUS, String V_PERCODE);

    /**
     * 删除组织机构
     *
     * @param I_ID i_id
     */
    int deleteDept(String I_ID);

}