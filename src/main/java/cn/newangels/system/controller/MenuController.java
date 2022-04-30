package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.ListToTreeUtil;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统菜单
 *
 * @author: TangLiang
 * @date: 2022/01/26 09:16
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载系统菜单
     */
    @GetMapping("loadMenu")
    @Log(title = "菜单管理", operateType = "加载系统菜单")
    public Map<String, Object> loadMenu(String I_ID) {
        return BaseUtils.loadSuccess(menuService.loadMenu(I_ID));
    }

    /**
     * 加载系统菜单树
     */
    @GetMapping("selectMenuTree")
    @Log(title = "菜单管理", operateType = "加载系统菜单树")
    public Map<String, Object> selectMenuTree() {
        List<Map<String, Object>> list = menuService.selectMenu(null, null, null, null);
        return BaseUtils.success(ListToTreeUtil.listToTree(list, (m) -> "-1".equals(m.get("I_PID")), (m) -> m.get("I_ID"), (n) -> n.get("I_PID"), "isLeaf"));
    }

    /**
     * 加载人员菜单树
     */
    @GetMapping("selectPersonMenuTree")
    @Log(title = "菜单管理", operateType = "加载人员菜单树")
    public Map<String, Object> selectPersonMenuTree(String V_PERCODE, String V_SYSTYPE) {
        List<Map<String, Object>> list = menuService.selectPersonMenu(V_PERCODE, V_SYSTYPE);
        List<Map<String, Object>> treeList = ListToTreeUtil.listToTree(list, (m) -> "-1".equals(m.get("I_PID")), (m) -> m.get("I_ID"), (n) -> n.get("I_PID"), "isLeaf", true);
        Map<String, Object> result = new HashMap<>(4);
        result.put(BaseUtils.DATA, treeList);
        result.put("menuList", list.stream().map(m -> m.get("V_ADDRESS")).filter(Objects::nonNull).collect(Collectors.toList()));
        return BaseUtils.success(result);
    }

    /**
     * 查询系统菜单
     */
    @GetMapping("selectMenu")
    @Log(title = "菜单管理", operateType = "查询系统菜单")
    public Map<String, Object> selectMenuTree(String I_PID, String V_CODE, String V_NAME, String V_SYSTYPE) {
        return BaseUtils.success(menuService.selectMenu(I_PID, V_CODE, V_NAME, V_SYSTYPE));
    }

    /**
     * 新增系统菜单
     */
    @PostMapping("insertMenu")
    @Log(title = "菜单管理", operateType = "新增系统菜单")
    public Map<String, Object> insertMenu(String V_NAME, String I_PID, String V_ADDRESS, String V_ADDRESS_ICO, String V_SYSTYPE, Integer I_ORDER, String V_PERCODE) {
        if (menuService.insertMenu(String.valueOf(snowflakeIdWorker.nextId()), V_NAME, I_PID, V_ADDRESS, V_ADDRESS_ICO, V_SYSTYPE, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("新增系统菜单失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改系统菜单
     */
    @PostMapping("updateMenu")
    @Log(title = "菜单管理", operateType = "修改系统菜单")
    public Map<String, Object> updateMenu(String I_ID, String V_NAME, String V_ADDRESS, String V_ADDRESS_ICO, String V_SYSTYPE, Integer I_ORDER, String V_PERCODE) {
        if (menuService.updateMenu(I_ID, V_NAME, V_ADDRESS, V_ADDRESS_ICO, V_SYSTYPE, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("修改系统菜单失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除系统菜单
     */
    @PostMapping("deleteMenu")
    @Log(title = "菜单管理", operateType = "删除系统菜单")
    public Map<String, Object> deleteMenu(String I_ID) {
        if (menuService.deleteMenu(I_ID) == 0) {
            return BaseUtils.failed("删除系统菜单失败");
        }
        return BaseUtils.success();
    }

}
