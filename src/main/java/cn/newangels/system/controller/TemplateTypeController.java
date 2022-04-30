package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.TemplateTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 模板类型
 *
 * @author: JinHongKe
 * @date: 2022/02/14
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class TemplateTypeController {
    private final TemplateTypeService templateTypeService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载模板类型
     */
    @GetMapping("loadTemplateType")
    @Log(title = "模板类型管理", operateType = "加载模板类型详情信息")
    public Map<String, Object> loadTemplateType(String I_ID) {
        return BaseUtils.loadSuccess(templateTypeService.loadTemplateType(I_ID));
    }

    /**
     * 查询模板类型
     */
    @GetMapping("selectTemplateType")
    @Log(title = "模板类型管理", operateType = "查询模板类型")
    public Map<String, Object> selectTemplateType(String V_CODE, String V_NAME, String V_STATUS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = templateTypeService.selectTemplateType(V_CODE, V_NAME, V_STATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = templateTypeService.countTemplateType(V_CODE, V_NAME, V_STATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增模板类型
     */
    @PostMapping("insertTemplateType")
    @Log(title = "模板类型管理", operateType = "新增模板类型")
    public Map<String, Object> insertTemplateType(String V_CODE, String V_NAME, String I_CONNUMBER, Integer I_ORDER, String V_PERCODE) {
        if (templateTypeService.insertTemplateType(String.valueOf(snowflakeIdWorker.nextId()), V_CODE, V_NAME, I_CONNUMBER, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("新增模板类型失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改模板类型
     */
    @PostMapping("updateTemplateType")
    @Log(title = "模板类型管理", operateType = "修改模板类型")
    public Map<String, Object> updateTemplateType(String I_ID, String V_CODE, String V_NAME, String I_CONNUMBER, Integer I_ORDER, String V_PERCODE) {
        if (templateTypeService.updateTemplateType(I_ID, V_CODE, V_NAME, I_CONNUMBER, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("修改模板类型失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改模板类型状态
     */
    @PostMapping("updateTemplateTypeStatus")
    @Log(title = "模板类型管理", operateType = "修改模板类型状态")
    public Map<String, Object> updateTemplateTypeStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (templateTypeService.updateTemplateTypeStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改模板类型状态失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除模板类型
     */
    @PostMapping("deleteTemplateType")
    @Log(title = "模板类型管理", operateType = "删除模板类型")
    public Map<String, Object> deleteTemplateType(String I_ID) {
        if (templateTypeService.deleteTemplateType(I_ID) == 0) {
            return BaseUtils.failed("删除模板类型失败");
        }
        return BaseUtils.success();
    }

}
