package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.PersonToMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 人员专有菜单
 *
 * @author: ll
 * @date: 2022/02/11 14:54
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PersonToMenuController {
    private final PersonToMenuService personToMenuService;

    /**
     * 查询人员专有菜单
     */
    @GetMapping("selectPersonToMenu")
    @Log(title = "人员专有菜单管理", operateType = "查询人员专有菜单")
    public Map<String, Object> selectPersonToMenu(String V_PERCODE_FORM, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = personToMenuService.selectPersonToMenu(V_PERCODE_FORM, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = personToMenuService.countPersonToMenu(V_PERCODE_FORM);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增人员专有菜单
     */
    @PostMapping("insertPersonToMenuBatch")
    @Log(title = "人员专有菜单管理", operateType = "新增人员专有菜单")
    public Map<String, Object> insertPersonToMenuBatch(String V_PERCODE_FORM, @RequestParam List<String> V_MENUCODELIST, String V_PERCODE) {
        if (personToMenuService.insertPersonToMenuBatch(V_PERCODE_FORM, V_MENUCODELIST, V_PERCODE) == 0) {
            return BaseUtils.failed("新增人员专有菜单失败");
        }
        return BaseUtils.success();
    }


    /**
     * 删除人员专有菜单
     */
    @PostMapping("deletePersonToMenu")
    @Log(title = "人员专有菜单管理", operateType = "删除人员专有菜单")
    public Map<String, Object> deletePersonToMenu(String I_ID) {
        if (personToMenuService.deletePersonToMenu(I_ID) == 0) {
            return BaseUtils.failed("删除人员专有菜单失败");
        }
        return BaseUtils.success();
    }

}
