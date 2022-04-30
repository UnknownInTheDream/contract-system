package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.PersonToPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 人员岗位
 *
 * @author: TangLiang
 * @date: 2022/02/07 14:39
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PersonToPostController {
    private final PersonToPostService personToPostService;

    /**
     * 查询人员岗位
     */
    @GetMapping("selectPersonToPost")
    @Log(title = "人员岗位管理", operateType = "查询人员岗位")
    public Map<String, Object> selectPersonToPost(String V_PERCODE_FORM, String V_POSTCODE, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = personToPostService.selectPersonToPost(V_PERCODE_FORM, V_POSTCODE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = personToPostService.countPersonToPost(V_PERCODE_FORM, V_POSTCODE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增人员岗位
     */
    @PostMapping("insertPersonToPostBatch")
    @Log(title = "人员岗位管理", operateType = "新增人员岗位")
    public Map<String, Object> insertPersonToPostBatch(String V_PERCODE_FORM, @RequestParam List<String> V_POSTCODELIST, String V_PERCODE) {
        if (personToPostService.insertPersonToPostBatch(V_PERCODE_FORM, V_POSTCODELIST, V_PERCODE) == 0) {
            return BaseUtils.failed("新增人员岗位失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除人员岗位
     */
    @PostMapping("deletePersonToPost")
    @Log(title = "人员岗位管理", operateType = "删除人员岗位")
    public Map<String, Object> deletePersonToPost(String I_ID) {
        if (personToPostService.deletePersonToPost(I_ID) == 0) {
            return BaseUtils.failed("删除人员岗位失败");
        }
        return BaseUtils.success();
    }

}
