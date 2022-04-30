package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.ContractorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 承揽方
 *
 * @author: JinHongKe
 * @date: 2022/01/24
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class ContractorController {
    private final ContractorService contractorService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载承揽方
     */
    @GetMapping("loadContractor")
    @Log(title = "承揽方管理", operateType = "加载承揽方详情信息")
    public Map<String, Object> loadContractor(String I_ID) {
        return BaseUtils.loadSuccess(contractorService.loadContractor(I_ID));
    }

    /**
     * 查询承揽方
     */
    @GetMapping("selectContractor")
    @Log(title = "承揽方管理", operateType = "查询承揽方")
    public Map<String, Object> selectContractor(String V_NAME, String V_STATUS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = contractorService.selectContractor(V_NAME, V_STATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = contractorService.countContractor(V_NAME, V_STATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增承揽方
     */
    @PostMapping("insertContractor")
    @Log(title = "承揽方管理", operateType = "新增承揽方")
    public Map<String, Object> insertContractor(String V_NAME, String V_ADDRESS, String V_LEGAL, String V_REPRESENTITIVE, String V_PHONE, String V_BANK, String V_ACCOUNT, String V_NATURE, String I_REGEREDCAPITAL, String V_LICENSE, String V_CREDIT, String V_PERCODE) {
        if (contractorService.insertContractor(String.valueOf(snowflakeIdWorker.nextId()), V_NAME, V_ADDRESS, V_LEGAL, V_REPRESENTITIVE, V_PHONE, V_BANK, V_ACCOUNT, V_NATURE, new BigDecimal(StringUtils.isEmpty(I_REGEREDCAPITAL) ? "0" : I_REGEREDCAPITAL), V_LICENSE, V_CREDIT, V_PERCODE) == 0) {
            return BaseUtils.failed("新增承揽方失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改承揽方
     */
    @PostMapping("updateContractor")
    @Log(title = "承揽方管理", operateType = "修改承揽方")
    public Map<String, Object> updateContractor(String I_ID, String V_NAME, String V_ADDRESS, String V_LEGAL, String V_REPRESENTITIVE, String V_PHONE, String V_BANK, String V_ACCOUNT, String V_NATURE, String I_REGEREDCAPITAL, String V_LICENSE, String V_CREDIT, String V_PERCODE) {
        if (contractorService.updateContractor(I_ID, V_NAME, V_ADDRESS, V_LEGAL, V_REPRESENTITIVE, V_PHONE, V_BANK, V_ACCOUNT, V_NATURE, new BigDecimal(StringUtils.isEmpty(I_REGEREDCAPITAL) ? "0" : I_REGEREDCAPITAL), V_LICENSE, V_CREDIT, V_PERCODE) == 0) {
            return BaseUtils.failed("修改承揽方失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改承揽方状态
     */
    @PostMapping("updateContractorStatus")
    @Log(title = "承揽方管理", operateType = "修改承揽方状态")
    public Map<String, Object> updateContractorStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (contractorService.updateContractorStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改承揽方状态失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除承揽方
     */
    @PostMapping("deleteContractor")
    @Log(title = "承揽方管理", operateType = "删除承揽方")
    public Map<String, Object> deleteContractor(String I_ID) {
        if (contractorService.deleteContractor(I_ID) == 0) {
            return BaseUtils.failed("删除承揽方失败");
        }
        return BaseUtils.success();
    }

}
