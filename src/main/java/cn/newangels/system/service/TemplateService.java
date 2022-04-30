package cn.newangels.system.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 合同模板
 *
 * @author: MengLuLu
 * @date: 2022/2/24 9:55
 * @since: 1.0
 */
public interface TemplateService {

    /**
     * 加载合同模板
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadTemplate(String I_ID);

    /**
     * 根据合同模板生成html
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadTemplateHtmlByWord(String I_ID);

    /**
     * 加载合同模板html
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadTemplateHtml(String I_ID);

    /**
     * 查询合同模板
     *
     * @param V_TYPEID 模版类型
     * @param V_NAME   模版名称
     * @param V_STATUS 状态
     * @param current  当前页数
     * @param pageSize 每次显示数量
     */
    List<Map<String, Object>> selectTemplate(String V_TYPEID, String V_NAME, String V_STATUS, Integer current, Integer pageSize);

    /**
     * 查询合同模板数量
     *
     * @param V_TYPEID 模版类型
     * @param V_NAME   模版类型名称
     * @param V_STATUS 状态
     */
    int countTemplate(String V_TYPEID, String V_NAME, String V_STATUS);

    /**
     * 新增合同模板
     *
     * @param I_ID          i_id
     * @param multipartFile 文件流
     * @param V_NAME        模版名称
     * @param V_DESCRIPTION 模板描述
     * @param V_TYPEID      模板类型
     * @param V_TEMPTYPE    是否集团模版
     * @param V_HTML        html代码
     * @param V_PER_EDIT    最后修改人
     */
    int insertTemplate(String I_ID, MultipartFile multipartFile, String V_NAME, String V_DESCRIPTION, String V_TYPEID, String V_TEMPTYPE, String V_HTML, String V_PER_EDIT) throws IOException;

    /**
     * 修改合同模板
     *
     * @param I_ID          i_id
     * @param multipartFile 文件流
     * @param V_NAME        模版名称
     * @param V_DESCRIPTION 模板描述
     * @param V_TYPEID      模板类型
     * @param V_TEMPTYPE    是否集团模版
     * @param V_HTML        html代码
     * @param V_PER_EDIT    最后修改人
     */
    int updateTemplate(String I_ID, MultipartFile multipartFile, String V_NAME, String V_DESCRIPTION, String V_TYPEID, String V_TEMPTYPE, String V_HTML, String V_PER_EDIT) throws IOException;

    /**
     * 修改合同模板状态
     *
     * @param I_ID       id
     * @param V_STATUS   状态1/0
     * @param V_PER_EDIT 最后修改人
     */
    int updateTemplateStatus(String I_ID, String V_STATUS, String V_PER_EDIT);

    /**
     * 删除合同模板
     *
     * @param I_ID       i_id
     * @param V_URL      url
     * @param V_PER_EDIT 操作人编码
     */
    int deleteTemplate(String I_ID, String V_URL, String V_PER_EDIT);

    /**
     * 下载合同模版
     *
     * @param I_ID       i_id
     * @param V_URL      url
     * @param V_FILENAME 文件名
     * @param V_PERCODE  人员编码
     * @param request    request
     * @param response   response
     */
    void downloadTemplate(String I_ID, String V_URL, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException;

    /**
     * 加载合同模板文件
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadTemplateFile(String I_ID);

}
