package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.PersonToDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 人员组织机构
 *
 * @author: TangLiang
 * @date: 2022/02/08 09:50
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PersonToDeptController {
    private final PersonToDeptService personToDeptService;

    /**
     * 查询人员组织机构
     */
    @GetMapping("selectPersonToDept")
    @Log(title = "人员组织机构管理", operateType = "查询人员组织机构")
    public Map<String, Object> selectPersonToDept(String V_PERCODE_FORM, String V_ORGCODE, String V_DEPTCODE, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = personToDeptService.selectPersonToDept(V_PERCODE_FORM, V_ORGCODE, V_DEPTCODE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = personToDeptService.countPersonToDept(V_PERCODE_FORM, V_ORGCODE, V_DEPTCODE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增人员组织机构
     */
    @PostMapping("insertPersonToDeptBatch")
    @Log(title = "人员组织机构管理", operateType = "新增人员组织机构")
    public Map<String, Object> insertPersonToDeptBatch(@RequestParam List<String> V_PERCODE_FORMLIST, String V_ORGCODE, String V_DEPTCODE, String V_PERCODE) {
        if (personToDeptService.insertPersonToDeptBatch(V_PERCODE_FORMLIST, V_ORGCODE, V_DEPTCODE, V_PERCODE) == 0) {
            return BaseUtils.failed("新增人员组织机构失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除人员组织机构
     */
    @PostMapping("deletePersonToDept")
    @Log(title = "人员组织机构管理", operateType = "删除人员组织机构")
    public Map<String, Object> deletePersonToDept(String I_ID) {
        if (personToDeptService.deletePersonToDept(I_ID) == 0) {
            return BaseUtils.failed("删除人员组织机构失败");
        }
        return BaseUtils.success();
    }

}
