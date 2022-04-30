package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.exception.UnExpectedResultException;
import cn.newangels.system.feign.MinioService;
import cn.newangels.system.service.ContractRiderService;
import cn.newangels.system.util.MinioUtil;
import feign.Response;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author TangLiang
 * @date 2022/03/03 10:50
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ContractRiderServiceImpl implements ContractRiderService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final MinioService minioService;

    @Override
    public Map<String, Object> loadContractRiderBlob(String I_ID) {
        String sql = "select B_FILE from CON_RIDER where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectContractRider(String V_GUID, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContractRider(false, V_GUID);
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
    public int countContractRider(String V_GUID) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContractRider(true, V_GUID);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertContractRider(String I_ID, String V_GUID, MultipartFile multipartFile, String V_PERCODE) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String url = MinioUtil.buildFileUrl("contractRider", I_ID, fileName);
        byte[] B_FILE = multipartFile.getBytes();
        String sql = "insert into CON_RIDER (I_ID, V_GUID, V_FILENAME, V_FILEPATH, B_FILE) values(?, ?, ?, ?, ?)";
        int count = systemJdbcTemplate.update(sql, I_ID, V_GUID, fileName, url, B_FILE);
        Map<String, Object> result = minioService.putObject(url, multipartFile, V_PERCODE);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件上传服务调用失败");
        }
        return count;
    }

    @Override
    public int deleteContractRider(String I_ID, String V_FILEPATH, String V_PERCODE) {
        String sql = "delete from CON_RIDER where I_ID = ?";
        int count = systemJdbcTemplate.update(sql, I_ID);
        Map<String, Object> result = minioService.removeObject(V_FILEPATH, V_PERCODE);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件删除服务调用失败");
        }
        return count;
    }

    @Override
    public int deleteContractRiderByGuid(String V_GUID, String V_PERCODE) {
        List<Map<String, Object>> list = this.selectContractRider(V_GUID, null, null);
        List<String> objectNames = list.stream().map(m -> m.get("V_FILEPATH").toString()).collect(Collectors.toList());
        String sql = "delete from CON_RIDER where V_GUID = ?";
        int count = systemJdbcTemplate.update(sql, V_GUID);
        Map<String, Object> result = minioService.removeObjects(objectNames, V_PERCODE);
        if (result == null || !Boolean.parseBoolean(result.get("success").toString())) {
            throw new UnExpectedResultException("文件批量删除服务调用失败");
        }
        return count;
    }

    @Override
    public void downloadContractRider(String I_ID, String V_FILEPATH, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        @Cleanup InputStream inputStream = null;
        try {
            Response serviceResponse = minioService.downloadFile(V_FILEPATH, V_FILENAME, V_PERCODE);
            Response.Body body = serviceResponse.body();
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
            Map<String, Object> result = loadContractRiderBlob(I_ID);
            byte[] object = (byte[]) result.get("B_FILE");
            inputStream = new ByteArrayInputStream(object);
            BaseUtils.download(inputStream, V_FILENAME, request, response);
        }
    }

    @Override
    public void downloadContractRiderByGuid(String V_GUID, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        @Cleanup InputStream inputStream = null;
        List<Map<String, Object>> list = selectContractRider(V_GUID, null, null);
        if (list.size() == 0) {
            return;
        } else if (list.size() == 1) {
            downloadContractRider(list.get(0).get("I_ID").toString(), list.get(0).get("V_FILEPATH").toString(), list.get(0).get("V_FILENAME").toString(), V_PERCODE, request, response);
            return;
        }
        List<String> objectNames = list.stream().map(l -> l.get("V_FILEPATH").toString()).collect(Collectors.toList());
        String zipName = "【批量下载】" + objectNames.get(0).substring(objectNames.get(0).lastIndexOf("/") + 1) + "等.zip";
        try {
            Response serviceResponse = minioService.downloadRiderList(objectNames, zipName, V_PERCODE);
            Response.Body body = serviceResponse.body();
            if (body.length() == 0) {
                throw new IOException();
            }
            inputStream = body.asInputStream();
            @Cleanup BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            response.setHeader("Content-Disposition", "attachment; filename=" + BaseUtils.getFormatString(request, zipName));
            @Cleanup BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            int length = 0;
            byte[] temp = new byte[1024 * 10];
            while ((length = bufferedInputStream.read(temp)) != -1) {
                bufferedOutputStream.write(temp, 0, length);
            }
            bufferedOutputStream.flush();
        } catch (Exception e) {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment;filename=" + BaseUtils.getFormatString(request, zipName));
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            for (Map<String, Object> map : list) {
                Map<String, Object> rider = loadContractRiderBlob(map.get("I_ID").toString());
                String fileName = map.get("V_FILENAME").toString();
                byte[] object = (byte[]) rider.get("B_FILE");
                @Cleanup InputStream in = new ByteArrayInputStream(object);
                //创建输入流读取文件
                @Cleanup BufferedInputStream bis = new BufferedInputStream(in);
                //将文件写入zip内，即将文件进行打包
                zos.putNextEntry(new ZipEntry(fileName));
                //写入文件的方法，同上
                int size = 0;
                byte[] buffer = new byte[4096];
                //设置读取数据缓存大小
                while ((size = bis.read(buffer)) > 0) {
                    zos.write(buffer, 0, size);
                }
                //关闭输入输出流
                zos.closeEntry();
            }
            zos.close();
        }
    }

    private BaseSqlCriteria buildSqlCriteriaContractRider(boolean count, String V_GUID) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from CON_RIDER where 1 = 1";
        } else {
            sql = "select I_ID, V_GUID, V_FILENAME, V_FILEPATH from CON_RIDER where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_GUID)) {
            sql += " and V_GUID = :V_GUID";
            paramMap.put("V_GUID", V_GUID);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
