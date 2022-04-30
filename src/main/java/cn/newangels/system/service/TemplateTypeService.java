package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 模板类型
 *
 * @author: JinHongKe
 * @date: 2022/2/14
 * @since: 1.0
 */
public interface TemplateTypeService {

    /**
     * 加载模板类型
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadTemplateType(String I_ID);

    /**
     * 查询模板类型
     *
     * @param V_CODE   模版类型编码
     * @param V_NAME   模版类型名称
     * @param V_STATUS 状态
     * @param current  当前页数
     * @param pageSize 每次显示数量
     */
    List<Map<String, Object>> selectTemplateType(String V_CODE, String V_NAME, String V_STATUS, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_CODE   模版类型编码
     * @param V_NAME   模版类型名称
     * @param V_STATUS 状态
     */
    int countTemplateType(String V_CODE, String V_NAME, String V_STATUS);

    /**
     * 新增模板类型
     *
     * @param I_ID        i_id
     * @param V_CODE      模版类型编码
     * @param V_NAME      模版类型名称
     * @param I_CONNUMBER 合同号
     * @param I_ORDER     排序
     * @param V_PER_EDIT  最后修改人
     */
    int insertTemplateType(String I_ID, String V_CODE, String V_NAME, String I_CONNUMBER, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 修改模板类型
     *
     * @param I_ID        i_id
     * @param V_CODE      模版类型编码
     * @param V_NAME      模版类型名称
     * @param I_CONNUMBER 合同号
     * @param I_ORDER     排序
     * @param V_PER_EDIT  最后修改人
     */
    int updateTemplateType(String I_ID, String V_CODE, String V_NAME, String I_CONNUMBER, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 修改模板类型状态
     *
     * @param I_ID       id
     * @param V_STATUS   状态1/0
     * @param V_PER_EDIT 最后修改人
     */
    int updateTemplateTypeStatus(String I_ID, String V_STATUS, String V_PER_EDIT);

    /**
     * 删除模板类型
     *
     * @param I_ID i_id
     */
    int deleteTemplateType(String I_ID);
}
