package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.PerToContractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 承揽方收藏
 *
 * @author: JinHongKe
 * @date: 2022/01/24
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PerToContractorController {
    private final PerToContractorService pertocontractorService;

    /**
     * 查询个人承揽方收藏
     */
    @GetMapping("selectPerToContractor")
    @Log(title = "承揽方收藏管理", operateType = "查询个人承揽方收藏")
    public Map<String, Object> selectPerToContractor(String V_NAME, String V_PERCODE, String CONTRACTOR_ID, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = pertocontractorService.selectPerToContractor(V_NAME, V_PERCODE, CONTRACTOR_ID, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = pertocontractorService.countPerToContractor(V_NAME, V_PERCODE, CONTRACTOR_ID);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增个人承揽方收藏
     */
    @PostMapping("insertPerToContractorBatch")
    @Log(title = "承揽方收藏管理", operateType = "新增个人承揽方收藏")
    public Map<String, Object> insertPerToContractorBatch(@RequestParam List<String> CONTRACTOR_IDLIST, String V_PERCODE) {
        if (pertocontractorService.insertPerToContractorBatch(CONTRACTOR_IDLIST, V_PERCODE, V_PERCODE) == 0) {
            return BaseUtils.failed("新增个人承揽方收藏失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除个人承揽方收藏
     */
    @PostMapping("deletePerToContractorBatch")
    @Log(title = "承揽方收藏管理", operateType = "删除个人承揽方收藏")
    public Map<String, Object> deletePerToContractorBatch(@RequestParam List<String> I_IDLIST) {
        if (pertocontractorService.deletePerToContractorBatch(I_IDLIST) == 0) {
            return BaseUtils.failed("删除个人承揽方收藏失败");
        }
        return BaseUtils.success();
    }
}
