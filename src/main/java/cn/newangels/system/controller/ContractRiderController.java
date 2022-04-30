package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.ContractRiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 合同附件
 * //todo 无用接口待删除
 *
 * @author: TangLiang
 * @date: 2022/03/03 10:50
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class ContractRiderController {
    private final ContractRiderService contractRiderService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 查询合同附件
     */
    @GetMapping("selectContractRider")
    @Log(title = "合同附件管理", operateType = "查询合同附件")
    public Map<String, Object> selectContractRider(String V_GUID, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = contractRiderService.selectContractRider(V_GUID, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = contractRiderService.countContractRider(V_GUID);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增合同附件
     */
    @PostMapping("insertContractRider")
    @Log(title = "合同附件管理", operateType = "新增合同附件")
    public Map<String, Object> insertContractRider(String V_GUID, MultipartFile multipartFile, String V_PERCODE) throws IOException {
        if (contractRiderService.insertContractRider(String.valueOf(snowflakeIdWorker.nextId()), V_GUID, multipartFile, V_PERCODE) == 0) {
            return BaseUtils.failed("新增合同附件失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除合同附件
     */
    @PostMapping("deleteContractRider")
    @Log(title = "合同附件管理", operateType = "删除合同附件")
    public Map<String, Object> deleteContractRider(String I_ID, String V_FILEPATH, String V_PERCODE) {
        if (contractRiderService.deleteContractRider(I_ID, V_FILEPATH, V_PERCODE) == 0) {
            return BaseUtils.failed("删除合同附件失败");
        }
        return BaseUtils.success();
    }

    /**
     * 下载合同附件
     */
    @GetMapping("downloadContractRider")
    @Log(title = "合同附件管理", operateType = "下载合同附件")
    public void downloadContractRider(String I_ID, String V_FILEPATH, String V_FILENAME, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        contractRiderService.downloadContractRider(I_ID, V_FILEPATH, V_FILENAME, V_PERCODE, request, response);
    }

    /**
     * 根据业务编码下载合同附件
     */
    @GetMapping("downloadContractRiderByGuid")
    @Log(title = "合同附件管理", operateType = "根据业务编码下载合同附件")
    public void downloadContractRiderByGuid(String V_GUID, String V_PERCODE, HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        contractRiderService.downloadContractRiderByGuid(V_GUID, V_PERCODE, request, response);
    }

}
