package cn.newangels.system.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 合同附件
 *
 * @author: TangLiang
 * @date: 2022/03/03 10:50
 * @since: 1.0
 */
public interface ContractRiderService {

    /**
     * 加载合同附件流
     *
     * @param I_ID id
     */
    Map<String, Object> loadContractRiderBlob(String I_ID);

    /**
     * 查询合同附件
     *
     * @param V_GUID   业务编码
     * @param current  当前页数
     * @param pageSize 每次显示数量
     */
    List<Map<String, Object>> selectContractRider(String V_GUID, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_GUID 业务编码
     */
    int countContractRider(String V_GUID);

    /**
     * 新增合同附件
     *
     * @param I_ID          id
     * @param V_GUID        业务编码
     * @param multipartFile 文件流
     * @param V_PERCODE     人员编码
     */
    int insertContractRider(String I_ID, String V_GUID, MultipartFile multipartFile, String V_PERCODE) throws IOException;

    /**
     * 删除合同附件
     *
     * @param I_ID       id
     * @param V_FILEPATH 文件地址
     * @param V_PERCODE  人员编码
     */
    int deleteContractRider(String I_ID, String V_FILEPATH, String V_PERCODE);

    /**
     * 根据合同编码删除合同附件
     *
     * @param V_GUID    id
     * @param V_PERCODE 人员编码
     */
    int deleteContractRiderByGuid(String V_GUID, String V_PERCODE);

    /**
     * 下载合同附件
     *
     * @param I_ID       i_id
     * @param V_FILEPATH url
     * @param V_FILENAME 文件名
     * @param V_PERCODE  人员编码
     * @param request    request
     * @param response   response
     */
    void downloadContractRider(String I_ID, String V_FILEPATH, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException;

    /**
     * 根据业务编码下载合同附件
     *
     * @param V_GUID    业务编码
     * @param V_PERCODE 人员编码
     * @param request   request
     * @param response  response
     */
    void downloadContractRiderByGuid(String V_GUID, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException;

}