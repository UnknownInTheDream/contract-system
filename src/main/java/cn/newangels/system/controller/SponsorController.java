package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.SponsorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 定作方
 *
 * @author: JinHongKe
 * @date: 2022/01/24
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class SponsorController {
    private final SponsorService sponsorService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载定作方
     */
    @GetMapping("loadSponsor")
    @Log(title = "定作方管理", operateType = "加载定作方详情信息")
    public Map<String, Object> loadSponsor(String I_ID) {
        return BaseUtils.loadSuccess(sponsorService.loadSponsor(I_ID));
    }

    /**
     * 查询定作方
     */
    @GetMapping("selectSponsor")
    @Log(title = "定作方管理", operateType = "查询定作方")
    public Map<String, Object> selectSponsor(String V_SPONSORCODE, String V_SPONSORNAME, String V_STATUS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = sponsorService.selectSponsor(V_SPONSORCODE, V_SPONSORNAME, V_STATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = sponsorService.countSponsor(V_SPONSORCODE, V_SPONSORNAME, V_STATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增定作方
     */
    @PostMapping("insertSponsor")
    @Log(title = "定作方管理", operateType = "新增定作方")
    public Map<String, Object> insertSponsor(String V_SPONSORCODE, String V_SPONSORNAME, String V_SIMPLENAME, String V_OFFICER, String V_PHONE, Integer I_ORDER, String V_PERCODE) {
        if (sponsorService.insertSponsor(String.valueOf(snowflakeIdWorker.nextId()), V_SPONSORCODE, V_SPONSORNAME, V_SIMPLENAME, V_OFFICER, V_PHONE, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("新增定作方失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改定作方
     */
    @PostMapping("updateSponsor")
    @Log(title = "定作方管理", operateType = "修改定作方")
    public Map<String, Object> updateSponsor(String I_ID, String V_SPONSORCODE, String V_SPONSORNAME, String V_SIMPLENAME, String V_OFFICER, String V_PHONE, Integer I_ORDER, String V_PERCODE) {
        if (sponsorService.updateSponsor(I_ID, V_SPONSORCODE, V_SPONSORNAME, V_SIMPLENAME, V_OFFICER, V_PHONE, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("修改定作方失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改定作方状态
     */
    @PostMapping("updateSponsorStatus")
    @Log(title = "定作方管理", operateType = "修改定作方状态")
    public Map<String, Object> updateSponsorStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (sponsorService.updateSponsorStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改定作方状态失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除定作方
     */
    @PostMapping("deleteSponsor")
    @Log(title = "定作方管理", operateType = "删除定作方")
    public Map<String, Object> deleteSponsor(String I_ID) {
        if (sponsorService.deleteSponsor(I_ID) == 0) {
            return BaseUtils.failed("删除定作方失败");
        }
        return BaseUtils.success();
    }

}
