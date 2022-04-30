package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 专业
 *
 * @author: ll
 * @date: 2022/02/11 10:31
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;

    /**
     * 查询专业
     */
    @GetMapping("selectMajor")
    @Log(title = "专业管理", operateType = "查询专业")
    public Map<String, Object> selectMajor(String V_DEPTCODE, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = majorService.selectMajor(V_DEPTCODE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = majorService.countMajor(V_DEPTCODE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增专业
     */
    @PostMapping("insertMajorBatch")
    @Log(title = "专业管理", operateType = "新增专业")
    public Map<String, Object> insertMajorBatch(@RequestParam List<String> V_NAMELIST, String V_DEPTCODE, String V_PERCODE) {
        if (majorService.insertMajorBatch(V_NAMELIST, V_DEPTCODE, V_PERCODE) == 0) {
            return BaseUtils.failed("新增专业失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除专业
     */
    @PostMapping("deleteMajor")
    @Log(title = "专业管理", operateType = "删除专业")
    public Map<String, Object> deleteMajor(String I_ID) {
        if (majorService.deleteMajor(I_ID) == 0) {
            return BaseUtils.failed("删除专业失败");
        }
        return BaseUtils.success();
    }

}
