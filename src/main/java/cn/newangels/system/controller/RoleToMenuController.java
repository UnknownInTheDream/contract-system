package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.RoleToMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 角色对应菜单
 *
 * @author: JinHongKe
 * @date: 2022/02/10
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class RoleToMenuController {
    private final RoleToMenuService roleToMenuService;

    /**
     * 查询角色对应菜单
     */
    @GetMapping("selectRoleToMenu")
    @Log(title = "角色对应菜单管理", operateType = "查询角色对应菜单")
    public Map<String, Object> selectRoleToMenu(String V_ORLECODE, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = roleToMenuService.selectRoleToMenu(V_ORLECODE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = roleToMenuService.countRoleToMenu(V_ORLECODE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增角色对应菜单
     */
    @PostMapping("insertRoleToMenuBatch")
    @Log(title = "角色对应菜单管理", operateType = "新增角色对应菜单")
    public Map<String, Object> insertRoleToMenuBatch(String V_ORLECODE, @RequestParam List<String> V_MENUCODELIST, String V_PERCODE) {
        if (roleToMenuService.insertRoleToMenuBatch(V_ORLECODE, V_MENUCODELIST, V_PERCODE) == 0) {
            return BaseUtils.failed("新增角色对应菜单失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除角色对应菜单
     */
    @PostMapping("deleteRoleToMenu")
    @Log(title = "角色对应菜单管理", operateType = "删除角色对应菜单")
    public Map<String, Object> deleteRole(String I_ID) {
        if (roleToMenuService.deleteRoleToMenu(I_ID) == 0) {
            return BaseUtils.failed("删除角色对应菜单失败");
        }
        return BaseUtils.success();
    }

}
