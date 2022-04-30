package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.exception.UnExpectedResultException;
import cn.newangels.system.feign.MinioService;
import cn.newangels.system.service.TemplateService;
import cn.newangels.system.util.MinioUtil;
import com.aspose.words.Document;
import com.aspose.words.HtmlSaveOptions;
import com.aspose.words.SaveFormat;
import feign.Response;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同模板
 *
 * @author: MengLuLu
 * @date: 2022/2/24 9:55
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final MinioService minioService;

    @Override
    public Map<String, Object> loadTemplate(String I_ID) {
        String sql = "select I_ID, V_NAME, V_DESCRIPTION, V_TYPEID, V_URL, V_STATUS, V_TEMPTYPE, V_FILENAME, V_HTML, D_DATE_EDIT from CON_TEMPLATE where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public Map<String, Object> loadTemplateHtmlByWord(String I_ID) {
        Map<String, Object> result = loadTemplateFile(I_ID);
        byte[] object = (byte[]) result.get("B_TEMPLATE");
        InputStream ins = new ByteArrayInputStream(object);
        ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();
        String htmlText = "";
        try {
            Document doc = new Document(ins);
            HtmlSaveOptions opts = new HtmlSaveOptions(SaveFormat.HTML);
            opts.setExportXhtmlTransitional(true);
            opts.setExportImagesAsBase64(true);
            opts.setExportPageSetup(true);
            doc.save(htmlStream, opts);
            htmlText = new String(htmlStream.toByteArray(), StandardCharsets.UTF_8);
            htmlStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>(4);
        map.put("V_HTML", htmlText);
        return map;
    }

    @Override
    public Map<String, Object> loadTemplateHtml(String I_ID) {
        String sql = "select V_HTML from CON_TEMPLATE where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectTemplate(String V_TYPEID, String V_NAME, String V_STATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaTemplateType(false, V_TYPEID, V_NAME, V_STATUS);
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        if (current != null && pageSize != null && pageSize > 0) {
            int start = (current - 1) * pageSize + 1;
            int end = current * pageSize;
            sql = "select * from (select FULLTABLE.*, ROWNUM RN from (" + sql + ") FULLTABLE where ROWNUM <= " + end + ") where RN >= " + start;
        }
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @Override
    public int countTemplate(String V_TYPEID, String V_NAME, String V_STATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaTemplateType(true, V_TYPEID, V_NAME, V_STATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertTemplate(String I_ID, MultipartFile multipartFile, String V_NAME, String V_DESCRIPTION, String V_TYPEID, String V_TEMPTYPE, String V_HTML, String V_PER_EDIT) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String url = MinioUtil.buildFileUrl("template", I_ID, fileName);
        byte[] B_FILE = multipartFile.getBytes();
        String sql = "insert into CON_TEMPLATE (I_ID, V_NAME, V_DESCRIPTION, V_TYPEID, V_URL, V_TEMPTYPE, V_FILENAME, B_TEMPLATE, V_HTML, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int count = systemJdbcTemplate.update(sql, I_ID, V_NAME, V_DESCRIPTION, V_TYPEID, url, V_TEMPTYPE, fileName, B_FILE, V_HTML, V_PER_EDIT);
        Map<String, Object> result = minioService.putObject(url, multipartFile, V_PER_EDIT);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件上传服务调用失败");
        }
        return count;
    }

    @Override
    public int updateTemplate(String I_ID, MultipartFile multipartFile, String V_NAME, String V_DESCRIPTION, String V_TYPEID, String V_TEMPTYPE, String V_HTML, String V_PER_EDIT) throws IOException {
        Map<String, Object> map = loadTemplate(I_ID);
        if (map.isEmpty()) {
            return 0;
        }
        int count;
        //上传文件不为空
        if (multipartFile != null && !multipartFile.isEmpty()) {
            byte[] B_FILE = multipartFile.getBytes();
            String fileName = multipartFile.getOriginalFilename();
            String url = MinioUtil.buildFileUrl("template", I_ID, fileName);
            String sql = "update CON_TEMPLATE set V_NAME = ?, V_DESCRIPTION = ?, V_TYPEID = ?, V_URL = ?, V_TEMPTYPE = ?, V_FILENAME = ?, B_TEMPLATE = ?, V_HTML = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
            count = systemJdbcTemplate.update(sql, V_NAME, V_DESCRIPTION, V_TYPEID, url, V_TEMPTYPE, fileName, B_FILE, V_HTML, V_PER_EDIT, I_ID);
            //文件名发生变动，则删除远程文件
            if (!fileName.equals(map.get("V_FILENAME"))) {
                minioService.removeObject(map.get("V_URL").toString(), V_PER_EDIT);
            }
            Map<String, Object> result = minioService.putObject(url, multipartFile, V_PER_EDIT);
            if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
                throw new UnExpectedResultException("文件上传服务调用失败");
            }
        } else {
            String sql = "update CON_TEMPLATE set V_NAME = ?, V_DESCRIPTION = ?, V_TYPEID = ?, V_TEMPTYPE = ?, V_HTML = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
            return systemJdbcTemplate.update(sql, V_NAME, V_DESCRIPTION, V_TYPEID, V_TEMPTYPE, V_HTML, V_PER_EDIT, I_ID);
        }
        return count;
    }

    @Override
    public int updateTemplateStatus(String I_ID, String V_STATUS, String V_PER_EDIT) {
        String sql = "update CON_TEMPLATE set V_STATUS = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PER_EDIT, I_ID);
    }

    @Override
    public int deleteTemplate(String I_ID, String V_URL, String V_PER_EDIT) {
        String sql = "delete from CON_TEMPLATE where I_ID = ?";
        int count = systemJdbcTemplate.update(sql, I_ID);
        Map<String, Object> result = minioService.removeObject(V_URL, V_PER_EDIT);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件删除服务调用失败");
        }
        return count;
    }

    @Override
    public void downloadTemplate(String I_ID, String V_URL, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        @Cleanup InputStream inputStream = null;
        try {
            Response serviceResponse = minioService.downloadFile(V_URL, V_FILENAME, V_PERCODE);
            Response.Body body = serviceResponse.body();
            if (body.length() == 0) {
                throw new IOException();
            }
            inputStream = body.asInputStream();
            @Cleanup BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            response.setHeader("Content-Disposition", "attachment; filename=" + BaseUtils.getFormatString(request, V_FILENAME));
            @Cleanup BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            int length = 0;
            byte[] temp = new byte[1024 * 10];
            while ((length = bufferedInputStream.read(temp)) != -1) {
                bufferedOutputStream.write(temp, 0, length);
            }
            bufferedOutputStream.flush();
        } catch (Exception e) {
            Map<String, Object> result = loadTemplateFile(I_ID);
            byte[] object = (byte[]) result.get("B_TEMPLATE");
            inputStream = new ByteArrayInputStream(object);
            BaseUtils.download(inputStream, V_FILENAME, request, response);
        }
    }

    @Override
    public Map<String, Object> loadTemplateFile(String I_ID) {
        String sql = "select V_FILENAME, B_TEMPLATE from CON_TEMPLATE where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    private BaseSqlCriteria buildSqlCriteriaTemplateType(boolean count, String V_TYPEID, String V_NAME, String V_STATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from CON_TEMPLATE where 1 = 1";
        } else {
            sql = "select I_ID, V_NAME, V_DESCRIPTION, V_TYPEID, V_URL, V_STATUS, V_TEMPTYPE, V_FILENAME, D_DATE_EDIT, I_ID as V_TEMPLATEID from CON_TEMPLATE where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_NAME)) {
            sql += " and V_NAME like '%' || :V_NAME || '%'";
            paramMap.put("V_NAME", V_NAME);
        }
        if (StringUtils.isNotEmpty(V_TYPEID)) {
            sql += " and V_TYPEID = :V_TYPEID";
            paramMap.put("V_TYPEID", V_TYPEID);
        }
        if (StringUtils.isNotEmpty(V_STATUS)) {
            sql += " and V_STATUS = :V_STATUS";
            paramMap.put("V_STATUS", V_STATUS);
        }
        if (!count) {
            sql += " order by V_TYPEID";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
