package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 人员常用模版
 *
 * @author: TangLiang
 * @date: 2022/03/07 16:27
 * @since: 1.0
 */
public interface PerToTemplateService {

    /**
     * 查询人员常用模版
     *
     * @param V_PERCODE    人员编码
     * @param V_TEMPLATEID 模版id
     * @param V_NAME       模板名称
     * @param current      当前页数
     * @param pageSize     每次显示数量
     */
    List<Map<String, Object>> selectPerToTemplate(String V_PERCODE, String V_TEMPLATEID, String V_NAME, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_PERCODE    人员编码
     * @param V_TEMPLATEID 模版id
     * @param V_NAME       模板名称
     */
    int countPerToTemplate(String V_PERCODE, String V_TEMPLATEID, String V_NAME);

    /**
     * 新增人员常用模版
     *
     * @param V_PERCODE        人员编码
     * @param V_TEMPLATEIDLIST 模版id集合
     */
    int insertPerToTemplateBatch(String V_PERCODE, List<String> V_TEMPLATEIDLIST);

    /**
     * 删除人员常用模版
     *
     * @param I_IDLIST id集合
     */
    int deletePerToTemplateBatch(List<String> I_IDLIST);

}