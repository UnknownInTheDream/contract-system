package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.exception.UnExpectedResultException;
import cn.newangels.system.feign.MinioService;
import cn.newangels.system.service.ContractFileService;
import cn.newangels.system.service.TemplateService;
import cn.newangels.system.util.MinioUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Bookmarks;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TangLiang
 * @date 2022/03/03 10:50
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ContractFileServiceImpl implements ContractFileService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final MinioService minioService;
    private final TemplateService templateService;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> loadContractFile(String V_GUID) {
        String sql = "select I_ID, V_GUID, V_FILENAME, V_FILEPATH from CON_FILE where V_GUID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, V_GUID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public Map<String, Object> loadContractFileJson(String V_GUID) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> result = new HashMap<>(4);
        List<Map<String, Object>> list = this.selectContractFileExtra(V_GUID, null, null);
        for (Map<String, Object> stringObjectMap : list) {
            map.put(stringObjectMap.get("V_KEY").toString(), BaseUtils.valueOf(stringObjectMap.get("V_VALUE")));
        }
        try {
            result.put("V_JSON", objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Object> loadContractFileBlob(String V_GUID) {
        String sql = "select B_FILE from CON_FILE where V_GUID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, V_GUID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectContractFile(String V_GUID, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContractFile(false, V_GUID);
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
    public List<Map<String, Object>> selectContractFileExtra(String V_GUID, String V_KEY, String V_VALUE) {
        String sql = "select * from CON_FILE_EXTRA where 1 = 1";
        Map<String, Object> paramMap = new HashMap<>(4);
        if (StringUtils.isNotEmpty(V_GUID)) {
            sql += " and V_GUID = :V_GUID";
            paramMap.put("V_GUID", V_GUID);
        }
        if (StringUtils.isNotEmpty(V_KEY)) {
            sql += " and V_KEY = :V_KEY";
            paramMap.put("V_KEY", V_KEY);
        }
        if (StringUtils.isNotEmpty(V_VALUE)) {
            sql += " and V_VALUE = :V_VALUE";
            paramMap.put("V_VALUE", V_VALUE);
        }
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @Override
    public int countContractFile(String V_GUID) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContractFile(true, V_GUID);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertContractFileByTemplate(String I_ID, String V_GUID, String V_TEMPLATEID, String V_PERCODE, String V_JSON) throws IOException {
        Map<String, Object> template = templateService.loadTemplateFile(V_TEMPLATEID);
        String fileName = template.get("V_FILENAME").toString();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] object = (byte[]) template.get("B_TEMPLATE");
        @Cleanup InputStream is = new ByteArrayInputStream(object);
        Map<String, Object> map = objectMapper.readValue(V_JSON, new TypeReference<Map<String, Object>>() {
        });
        @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
        String contentType;
        //json数据插入到word文件流中
        if ("doc".equals(fileType)) {
            HWPFDocument doc = new HWPFDocument(is);
            Bookmarks bookmarks = doc.getBookmarks();
            for (int i = 0; i < bookmarks.getBookmarksCount(); i++) {
                Range range = new Range(bookmarks.getBookmark(i).getStart(), bookmarks.getBookmark(i).getEnd(), doc);
                range.insertAfter(BaseUtils.valueOf(map.get(bookmarks.getBookmark(i).getName())));
            }
            doc.write(os);
            contentType = "application/msword";
        } else if ("docx".equals(fileType)) {
            XWPFDocument document = new XWPFDocument(is);
            List<XWPFParagraph> paragraphList = document.getParagraphs();
            for (XWPFParagraph xwpfParagraph : paragraphList) {
                CTP ctp = xwpfParagraph.getCTP();
                for (int i = 0; i < ctp.sizeOfBookmarkStartArray(); i++) {
                    CTBookmark bookmark = ctp.getBookmarkStartArray(i);
                    if (map.containsKey(bookmark.getName())) {
                        XWPFRun run = xwpfParagraph.createRun();
                        run.setText(BaseUtils.valueOf(map.get(bookmark.getName())));
                        Node firstNode = bookmark.getDomNode();
                        firstNode.appendChild(run.getCTR().getDomNode());
                    }
                }
            }
            document.write(os);
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else {
            throw new UnExpectedResultException("未知文件类型");
        }
        byte[] bytes = os.toByteArray();
        String url = MinioUtil.buildFileUrl("contract", V_GUID, fileName);
        String sql = "insert into CON_FILE (I_ID, V_GUID, V_FILENAME, V_FILEPATH, B_FILE) values(?, ?, ?, ?, ?)";
        int count = systemJdbcTemplate.update(sql, I_ID, V_GUID, fileName, url, bytes);
        //CON_FILE_EXTRA
        String delSql = "delete from CON_FILE_EXTRA where V_GUID = ?";
        systemJdbcTemplate.update(delSql, V_GUID);
        String extraInsSql = "insert into CON_FILE_EXTRA (V_GUID, V_KEY, V_VALUE) values(?, ?, ?)";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            systemJdbcTemplate.update(extraInsSql, V_GUID, entry.getKey(), entry.getValue());
        }
        Map<String, Object> result = minioService.putObjectBytes(url, bytes, contentType, V_PERCODE);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件上传服务调用失败");
        }
        return count;
    }

    @Override
    public int updateContractFile(String V_GUID, MultipartFile multipartFile, String V_PERCODE) throws IOException {
        String sql = "update CON_FILE set B_FILE = ? where V_GUID = ?";
        int count = systemJdbcTemplate.update(sql, multipartFile.getBytes(), V_GUID);
        Map<String, Object> map = this.loadContractFile(V_GUID);
        Map<String, Object> result = minioService.putObject(map.get("V_FILEPATH").toString(), multipartFile, V_PERCODE);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件上传服务调用失败");
        }
        return count;
    }

    @Override
    public int deleteContractFileByGuid(String V_GUID, String V_PERCODE) {
        Map<String, Object> map = this.loadContractFile(V_GUID);
        String sql = "delete from CON_FILE where V_GUID = ?";
        int count = systemJdbcTemplate.update(sql, V_GUID);
        Map<String, Object> result = minioService.removeObject(map.get("V_FILEPATH").toString(), V_PERCODE);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件删除服务调用失败");
        }
        return count;
    }

    @Override
    public int deleteContractFileExtraByGuid(String V_GUID, String V_PERCODE) {
        String sql = "delete from CON_FILE_EXTRA where V_GUID = ?";
        return systemJdbcTemplate.update(sql, V_GUID);
    }

    @Override
    public int deleteContractFile(String I_ID, String V_FILEPATH, String V_PERCODE) {
        String sql = "delete from CON_FILE where I_ID = ?";
        int count = systemJdbcTemplate.update(sql, I_ID);
        Map<String, Object> result = minioService.removeObject(V_FILEPATH, V_PERCODE);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件删除服务调用失败");
        }
        return count;
    }

    @Override
    public void downloadContractFile(String V_GUID, String V_FILEPATH, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException {
        @Cleanup InputStream inputStream = null;
        try {
            Response serviceResponse = minioService.downloadFile(V_FILEPATH, V_FILENAME, V_PERCODE);
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
            Map<String, Object> result = loadContractFileBlob(V_GUID);
            byte[] object = (byte[]) result.get("B_FILE");
            inputStream = new ByteArrayInputStream(object);
            BaseUtils.download(inputStream, V_FILENAME, request, response);
        }
    }

    private BaseSqlCriteria buildSqlCriteriaContractFile(boolean count, String V_GUID) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from CON_FILE where 1 = 1";
        } else {
            sql = "select I_ID, V_GUID, V_FILENAME, V_FILEPATH from CON_FILE where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_GUID)) {
            sql += " and V_GUID = :V_GUID";
            paramMap.put("V_GUID", V_GUID);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
