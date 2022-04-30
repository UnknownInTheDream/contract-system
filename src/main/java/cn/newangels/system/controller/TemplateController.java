package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
@RestController
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载合同模板
     */
    @GetMapping("loadTemplate")
    @Log(title = "合同模板管理", operateType = "加载合同模板")
    public Map<String, Object> loadTemplate(String I_ID) {
        return BaseUtils.loadSuccess(templateService.loadTemplate(I_ID));
    }

    /**
     * 根据合同模板生成html
     */
    @GetMapping("loadTemplateHtmlByWord")
    @Log(title = "合同模板管理", operateType = "根据合同模板生成html")
    public Map<String, Object> loadTemplateHtmlByWord(String I_ID) {
        return BaseUtils.loadSuccess(templateService.loadTemplateHtmlByWord(I_ID));
    }

    /**
     * 加载合同模板html代码
     */
    @GetMapping("loadTemplateHtml")
    @Log(title = "合同模板管理", operateType = "加载合同模板html代码")
    public Map<String, Object> loadTemplateHtml(String I_ID) {
        return BaseUtils.loadSuccess(templateService.loadTemplateHtml(I_ID));
    }

    /**
     * 查询合同模板
     */
    @GetMapping("selectTemplate")
    @Log(title = "合同模板管理", operateType = "查询合同模板")
    public Map<String, Object> selectTemplate(String V_TYPEID, String V_NAME, String V_STATUS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = templateService.selectTemplate(V_TYPEID, V_NAME, V_STATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = templateService.countTemplate(V_TYPEID, V_NAME, V_STATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增合同模板
     */
    @PostMapping("insertTemplate")
    @Log(title = "合同模板管理", operateType = "新增合同模板")
    public Map<String, Object> insertTemplate(MultipartFile multipartFile, String V_NAME, String V_DESCRIPTION, String V_TYPEID, String V_TEMPTYPE, String V_HTML, String V_PERCODE) throws IOException {
        if (templateService.insertTemplate(String.valueOf(snowflakeIdWorker.nextId()), multipartFile, V_NAME, V_DESCRIPTION, V_TYPEID, V_TEMPTYPE, V_HTML, V_PERCODE) == 0) {
            return BaseUtils.failed("新增合同模板失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改合同模板
     */
    @PostMapping("updateTemplate")
    @Log(title = "合同模板管理", operateType = "修改合同模板")
    public Map<String, Object> updateTemplate(String I_ID, @RequestParam(required = false) MultipartFile multipartFile, String V_NAME, String V_DESCRIPTION, String V_TYPEID, String V_TEMPTYPE, String V_HTML, String V_PERCODE) throws IOException {
        if (templateService.updateTemplate(I_ID, multipartFile, V_NAME, V_DESCRIPTION, V_TYPEID, V_TEMPTYPE, V_HTML, V_PERCODE) == 0) {
            return BaseUtils.failed("修改合同模板失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改合同模板状态
     */
    @PostMapping("updateTemplateStatus")
    @Log(title = "合同模板管理", operateType = "修改合同模板状态")
    public Map<String, Object> updateTemplateStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (templateService.updateTemplateStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改合同模板状态失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除合同模板
     */
    @PostMapping("deleteTemplate")
    @Log(title = "合同模板管理", operateType = "删除合同模板")
    public Map<String, Object> deleteTemplate(String I_ID, String V_URL, String V_PERCODE) {
        if (templateService.deleteTemplate(I_ID, V_URL, V_PERCODE) == 0) {
            return BaseUtils.failed("删除合同模板失败");
        }
        return BaseUtils.success();
    }

    /**
     * 下载模板
     */
    @GetMapping("downloadTemplate")
    @Log(title = "合同模板管理", operateType = "下载合同模板")
    public void downloadTemplate(String I_ID, String V_URL, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        templateService.downloadTemplate(I_ID, V_URL, V_FILENAME, V_PERCODE, request, response);
    }

}