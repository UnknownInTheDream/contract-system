package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.PerToTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 人员常用模版
 *
 * @author: TangLiang
 * @date: 2022/03/07 16:27
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PerToTemplateController {
    private final PerToTemplateService perToTemplateService;

    /**
     * 查询人员常用模版
     */
    @GetMapping("selectPerToTemplate")
    @Log(title = "人员常用模版管理", operateType = "查询人员常用模版")
    public Map<String, Object> selectPerToTemplate(String V_PERCODE, String V_TEMPLATEID, String V_NAME, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = perToTemplateService.selectPerToTemplate(V_PERCODE, V_TEMPLATEID, V_NAME, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = perToTemplateService.countPerToTemplate(V_PERCODE, V_TEMPLATEID, V_NAME);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增人员常用模版
     */
    @PostMapping("insertPerToTemplateBatch")
    @Log(title = "人员常用模版管理", operateType = "新增人员常用模版")
    public Map<String, Object> insertPerToTemplateBatch(String V_PERCODE, @RequestParam List<String> V_TEMPLATEIDLIST) {
        if (perToTemplateService.insertPerToTemplateBatch(V_PERCODE, V_TEMPLATEIDLIST) == 0) {
            return BaseUtils.failed("新增人员常用模版失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除人员常用模版
     */
    @PostMapping("deletePerToTemplateBatch")
    @Log(title = "人员常用模版管理", operateType = "删除人员常用模版")
    public Map<String, Object> deletePerToTemplateBatch(@RequestParam List<String> I_IDLIST) {
        if (perToTemplateService.deletePerToTemplateBatch(I_IDLIST) == 0) {
            return BaseUtils.failed("删除人员常用模版失败");
        }
        return BaseUtils.success();
    }

}
