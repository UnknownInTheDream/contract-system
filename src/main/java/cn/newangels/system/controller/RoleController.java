package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author: JinHongKe
 * @date: 2022/02/10
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载角色
     */
    @GetMapping("loadRole")
    @Log(title = "角色管理", operateType = "加载角色详情信息")
    public Map<String, Object> loadRole(String I_ID) {
        return BaseUtils.loadSuccess(roleService.loadRole(I_ID));
    }

    /**
     * 查询角色
     */
    @GetMapping("selectRole")
    @Log(title = "角色管理", operateType = "查询角色")
    public Map<String, Object> selectRole(String V_ORLECODE, String V_ORLENAME, String V_STATUS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = roleService.selectRole(V_ORLECODE, V_ORLENAME, V_STATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = roleService.countRole(V_ORLECODE, V_ORLENAME, V_STATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增角色
     */
    @PostMapping("insertRole")
    @Log(title = "角色管理", operateType = "新增角色")
    public Map<String, Object> insertRole(String V_ORLECODE, String V_ORLENAME, String V_PERCODE) {
        if (roleService.insertRole(String.valueOf(snowflakeIdWorker.nextId()), V_ORLECODE, V_ORLENAME, V_PERCODE) == 0) {
            return BaseUtils.failed("新增角色失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改角色
     */
    @PostMapping("updateRole")
    @Log(title = "角色管理", operateType = "修改角色")
    public Map<String, Object> updateRole(String I_ID, String V_ORLENAME, String V_PERCODE) {
        if (roleService.updateRole(I_ID, V_ORLENAME, V_PERCODE) == 0) {
            return BaseUtils.failed("修改角色失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改角色状态
     */
    @PostMapping("updateRoleStatus")
    @Log(title = "角色管理", operateType = "修改角色状态")
    public Map<String, Object> updateRoleStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (roleService.updateRoleStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改角色状态失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除角色
     */
    @PostMapping("deleteRole")
    @Log(title = "角色管理", operateType = "删除角色")
    public Map<String, Object> deleteRole(String I_ID) {
        if (roleService.deleteRole(I_ID) == 0) {
            return BaseUtils.failed("删除角色失败");
        }
        return BaseUtils.success();
    }

}
