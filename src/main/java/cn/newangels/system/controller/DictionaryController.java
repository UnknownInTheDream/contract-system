package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 字典
 *
 * @author: TangLiang
 * @date: 2022/01/22 16:39
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService dictionaryService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载字典
     */
    @GetMapping("loadDictionary")
    @Log(title = "字典管理", operateType = "加载字典详情信息")
    public Map<String, Object> loadDictionary(String I_ID) {
        return BaseUtils.loadSuccess(dictionaryService.loadDictionary(I_ID));
    }

    /**
     * 查询字典
     */
    @GetMapping("selectDictionary")
    @Log(title = "字典管理", operateType = "查询字典")
    public Map<String, Object> selectDictionary(String V_CODE, String V_NAME, String V_DICTIONARYTYPE, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = dictionaryService.selectDictionary(V_CODE, V_NAME, V_DICTIONARYTYPE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = dictionaryService.countDictionary(V_CODE, V_NAME, V_DICTIONARYTYPE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增字典
     */
    @PostMapping("insertDictionary")
    @Log(title = "字典管理", operateType = "新增字典")
    public Map<String, Object> insertDictionary(String V_CODE, String V_NAME, Integer I_ORDER, String V_DICTIONARYTYPE, String V_OTHER, String V_PERCODE) {
        if (dictionaryService.insertDictionary(String.valueOf(snowflakeIdWorker.nextId()), V_CODE, V_NAME, I_ORDER, V_DICTIONARYTYPE, V_OTHER, V_PERCODE) == 0) {
            return BaseUtils.failed("新增字典失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改字典
     */
    @PostMapping("updateDictionary")
    @Log(title = "字典管理", operateType = "修改字典")
    public Map<String, Object> updateDictionary(String I_ID, String V_CODE, String V_NAME, Integer I_ORDER, String V_DICTIONARYTYPE, String V_OTHER, String V_PERCODE) {
        if (dictionaryService.updateDictionary(I_ID, V_CODE, V_NAME, I_ORDER, V_DICTIONARYTYPE, V_OTHER, V_PERCODE) == 0) {
            return BaseUtils.failed("修改字典失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除字典
     */
    @PostMapping("deleteDictionary")
    @Log(title = "字典管理", operateType = "删除字典")
    public Map<String, Object> deleteDictionary(String I_ID) {
        if (dictionaryService.deleteDictionary(I_ID) == 0) {
            return BaseUtils.failed("删除字典失败");
        }
        return BaseUtils.success();
    }

}
