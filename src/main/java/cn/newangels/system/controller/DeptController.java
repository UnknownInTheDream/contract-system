package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.ListToTreeUtil;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 组织机构
 *
 * @author: TangLiang
 * @date: 2022/01/27 09:21
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class DeptController {
    private final DeptService deptService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载组织机构
     */
    @GetMapping("loadDept")
    @Log(title = "组织机构管理", operateType = "加载组织机构树")
    public Map<String, Object> loadDept(String I_ID) {
        return BaseUtils.loadSuccess(deptService.loadDept(I_ID));
    }

    /**
     * 查询组织机构
     */
    @GetMapping("selectDept")
    @Log(title = "组织机构管理", operateType = "查询组织机构")
    public Map<String, Object> selectDept(String V_DEPTCODE, String V_DEPTNAME, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_STATUS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = deptService.selectDept(V_DEPTCODE, V_DEPTNAME, V_DEPTTYPE, V_DEPTCODE_UP, V_STATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = deptService.countDept(V_DEPTCODE, V_DEPTNAME, V_DEPTTYPE, V_DEPTCODE_UP, V_STATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 加载组织机构树
     */
    @GetMapping("selectDeptTree")
    @Log(title = "组织机构管理", operateType = "加载组织机构树")
    public Map<String, Object> selectDeptTree() {
        List<Map<String, Object>> list = deptService.selectDept(null, null, null, null, null, null, null);
        return BaseUtils.success(ListToTreeUtil.listToTree(list, (m) -> "-1".equals(m.get("V_DEPTCODE_UP")), (m) -> m.get("V_DEPTCODE"), (n) -> n.get("V_DEPTCODE_UP"), "isLeaf"));
    }

    /**
     * 新增组织机构
     */
    @PostMapping("insertDept")
    @Log(title = "组织机构管理", operateType = "新增组织机构")
    public Map<String, Object> insertDept(String V_DEPTCODE, String V_DEPTNAME, String V_DEPTNAME_FULL, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_DLFR, String V_PRODUCE, String V_DQ, String V_PERCODE) {
        if (deptService.insertDept(String.valueOf(snowflakeIdWorker.nextId()), V_DEPTCODE, V_DEPTNAME, V_DEPTNAME_FULL, V_DEPTTYPE, V_DEPTCODE_UP, V_DLFR, V_PRODUCE, V_DQ, V_PERCODE) == 0) {
            return BaseUtils.failed("新增组织机构失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改组织机构
     */
    @PostMapping("updateDept")
    @Log(title = "组织机构管理", operateType = "修改组织机构")
    public Map<String, Object> updateDept(String I_ID, String V_DEPTCODE, String V_DEPTNAME, String V_DEPTNAME_FULL, String V_DEPTTYPE, String V_DLFR, String V_PRODUCE, String V_DQ, String V_PERCODE) {
        if (deptService.updateDept(I_ID, V_DEPTCODE, V_DEPTNAME, V_DEPTNAME_FULL, V_DEPTTYPE, V_DLFR, V_PRODUCE, V_DQ, V_PERCODE) == 0) {
            return BaseUtils.failed("修改组织机构失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改组织机构状态
     */
    @PostMapping("updateDeptStatus")
    @Log(title = "组织机构管理", operateType = "修改组织机构状态")
    public Map<String, Object> updateDeptStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (deptService.updateDeptStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改组织机构失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除组织机构
     */
    @PostMapping("deleteDept")
    @Log(title = "组织机构管理", operateType = "删除组织机构")
    public Map<String, Object> deleteDept(String I_ID) {
        if (deptService.deleteDept(I_ID) == 0) {
            return BaseUtils.failed("删除组织机构失败");
        }
        return BaseUtils.success();
    }

}
