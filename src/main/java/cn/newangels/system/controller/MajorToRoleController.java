package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.MajorToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 专业对应角色
 *
 * @author: TangLiang
 * @date: 2022/02/17 16:48
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class MajorToRoleController {
    private final MajorToRoleService majorToRoleService;

    /**
     * 查询专业对应角色
     */
    @GetMapping("selectMajorToRole")
    @Log(title = "专业对应角色管理", operateType = "查询专业对应角色")
    public Map<String, Object> selectMajorToRole(String V_ORLECODE, String V_MAJORID, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = majorToRoleService.selectMajorToRole(V_ORLECODE, V_MAJORID, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = majorToRoleService.countMajorToRole(V_ORLECODE, V_MAJORID);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增专业对应角色
     */
    @PostMapping("insertMajorToRoleBatch")
    @Log(title = "专业对应角色管理", operateType = "新增专业对应角色")
    public Map<String, Object> insertMajorToRoleBatch(@RequestParam List<String> V_ORLECODELIST, String V_MAJORID, String V_PERCODE) {
        if (majorToRoleService.insertMajorToRoleBatch(V_ORLECODELIST, V_MAJORID, V_PERCODE) == 0) {
            return BaseUtils.failed("新增专业对应角色失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除专业对应角色
     */
    @PostMapping("deleteMajorToRole")
    @Log(title = "专业对应角色管理", operateType = "删除专业对应角色")
    public Map<String, Object> deleteMajorToRole(String I_ID) {
        if (majorToRoleService.deleteMajorToRole(I_ID) == 0) {
            return BaseUtils.failed("删除专业对应角色失败");
        }
        return BaseUtils.success();
    }

}
