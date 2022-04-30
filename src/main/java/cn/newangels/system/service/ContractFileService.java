package cn.newangels.system.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 合同文件
 *
 * @author: TangLiang
 * @date: 2022/03/03 10:50
 * @since: 1.0
 */
public interface ContractFileService {

    /**
     * 加载合同文件
     *
     * @param V_GUID 合同编码
     */
    Map<String, Object> loadContractFile(String V_GUID);

    /**
     * 加载合同文件json
     *
     * @param V_GUID 合同编码
     */
    Map<String, Object> loadContractFileJson(String V_GUID);

    /**
     * 加载合同文件流
     *
     * @param V_GUID 合同编码
     */
    Map<String, Object> loadContractFileBlob(String V_GUID);

    /**
     * 查询合同文件
     *
     * @param V_GUID   合同id
     * @param current  当前页数
     * @param pageSize 每次显示数量
     */
    List<Map<String, Object>> selectContractFile(String V_GUID, Integer current, Integer pageSize);

    /**
     * 查询合同文件数据
     *
     * @param V_GUID  合同id
     * @param V_KEY   当前页数
     * @param V_VALUE 每次显示数量
     */
    List<Map<String, Object>> selectContractFileExtra(String V_GUID, String V_KEY, String V_VALUE);

    /**
     * 查询数量
     *
     * @param V_GUID 合同id
     */
    int countContractFile(String V_GUID);

    /**
     * 新增合同文件(模版)
     *
     * @param I_ID         id
     * @param V_GUID       合同id
     * @param V_TEMPLATEID 模版id
     * @param V_PERCODE    人员编码
     * @param V_JSON       json
     */
    int insertContractFileByTemplate(String I_ID, String V_GUID, String V_TEMPLATEID, String V_PERCODE, String V_JSON) throws IOException;

    /**
     * 修改合同文件
     *
     * @param V_GUID        合同id
     * @param multipartFile 文件流
     * @param V_PERCODE     人员编码
     */
    int updateContractFile(String V_GUID, MultipartFile multipartFile, String V_PERCODE) throws IOException;

    /**
     * 根据合同编码删除合同文件
     *
     * @param V_GUID    id
     * @param V_PERCODE 人员编码
     */
    int deleteContractFileByGuid(String V_GUID, String V_PERCODE);

    /**
     * 根据合同编码删除合同文件数据
     *
     * @param V_GUID    id
     * @param V_PERCODE 人员编码
     */
    int deleteContractFileExtraByGuid(String V_GUID, String V_PERCODE);

    /**
     * 根据合同编码删除合同文件
     *
     * @param I_ID       id
     * @param V_FILEPATH url
     * @param V_PERCODE  人员编码
     */
    int deleteContractFile(String I_ID, String V_FILEPATH, String V_PERCODE);

    /**
     * 下载合同文件
     *
     * @param V_GUID     合同编码
     * @param V_FILEPATH url
     * @param V_FILENAME 文件名
     * @param V_PERCODE  人员编码
     * @param request    request
     * @param response   response
     */
    void downloadContractFile(String V_GUID, String V_FILEPATH, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException;

}