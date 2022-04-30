package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 字典
 *
 * @author: TangLiang
 * @date: 2022/01/22 16:39
 * @since: 1.0
 */
public interface DictionaryService {

    /**
     * 加载字典
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadDictionary(String I_ID);

    /**
     * 查询字典
     *
     * @param V_CODE           编码
     * @param V_NAME           名称
     * @param V_DICTIONARYTYPE 字典分类
     * @param current          当前页数
     * @param pageSize         每次显示数量
     */
    List<Map<String, Object>> selectDictionary(String V_CODE, String V_NAME, String V_DICTIONARYTYPE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_CODE           编码
     * @param V_NAME           名称
     * @param V_DICTIONARYTYPE 字典分类
     */
    int countDictionary(String V_CODE, String V_NAME, String V_DICTIONARYTYPE);

    /**
     * 新增字典
     *
     * @param I_ID             i_id
     * @param V_CODE           编码
     * @param V_NAME           名称
     * @param I_ORDER          排序
     * @param V_DICTIONARYTYPE 字典分类
     * @param V_OTHER          特殊用途标记
     * @param V_PER_EDIT       最后修改人
     */
    int insertDictionary(String I_ID, String V_CODE, String V_NAME, Integer I_ORDER, String V_DICTIONARYTYPE, String V_OTHER, String V_PER_EDIT);

    /**
     * 修改字典
     *
     * @param I_ID             i_id
     * @param V_CODE           编码
     * @param V_NAME           名称
     * @param I_ORDER          排序
     * @param V_DICTIONARYTYPE 字典分类
     * @param V_OTHER          特殊用途标记
     * @param V_PER_EDIT       最后修改人
     */
    int updateDictionary(String I_ID, String V_CODE, String V_NAME, Integer I_ORDER, String V_DICTIONARYTYPE, String V_OTHER, String V_PER_EDIT);

    /**
     * 删除字典
     *
     * @param I_ID i_id
     */
    int deleteDictionary(String I_ID);

}