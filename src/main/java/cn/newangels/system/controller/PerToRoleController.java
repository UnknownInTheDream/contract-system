package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.PerToRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员角色管理
 *
 * @author: TangLiang
 * @date: 2022/2/10 10:21
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PerToRoleController {
    private final PerToRoleService perToRoleService;

    /**
     * 查询人员岗位
     */
    @GetMapping("selectPerToRole")
    @Log(title = "人员角色管理", operateType = "查询人员角色")
    public Map<String, Object> selectPerToRole(String V_PERCODE_FORM, String V_ORLECODE, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = perToRoleService.selectPerToRole(V_PERCODE_FORM, V_ORLECODE, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = perToRoleService.countPerToRole(V_PERCODE_FORM, V_ORLECODE);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增人员角色
     */
    @PostMapping("insertPerToRoleBatch")
    @Log(title = "人员角色管理", operateType = "新增人员角色")
    public Map<String, Object> insertPerToRoleBatch(String V_PERCODE_FORM, @RequestParam List<String> V_ORLECODELIST, String V_PERCODE) {
        if (perToRoleService.insertPerToRoleBatch(V_PERCODE_FORM, V_ORLECODELIST, V_PERCODE) == 0) {
            return BaseUtils.failed("新增人员角色失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除人员角色
     */
    @PostMapping("deletePerToRole")
    @Log(title = "人员角色管理", operateType = "删除人员角色")
    public Map<String, Object> deletePerToRole(String I_ID) {
        if (perToRoleService.deletePerToRole(I_ID) == 0) {
            return BaseUtils.failed("删除人员角色失败");
        }
        return BaseUtils.success();
    }

    /**
     * 导出人员角色
     */
    @GetMapping("exportPerToRole")
    @Log(title = "人员角色管理", operateType = "导出人员角色")
    public void exportPerToRole(String V_PERCODE_FORM, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = perToRoleService.selectPerToRole(V_PERCODE_FORM, null, null, null);
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(4);
        linkedHashMap.put("角色编码", "V_ORLECODE");
        linkedHashMap.put("角色名称", "V_ORLENAME");
        BaseUtils.dealCommonExcel(wb, sheet, list, linkedHashMap);
        BaseUtils.download(wb, "人员角色.xls", request, response);
    }

    /**
     * 导入人员角色
     */
    @PostMapping("importPerToRole")
    @Log(title = "人员角色管理", operateType = "导入人员角色")
    public Map<String, Object> importPerToRole(String V_PERCODE_FORM, String V_PERCODE, @RequestParam(value = "multipartFiles") MultipartFile multipartFile) throws IOException, InvalidFormatException {
        InputStream is = multipartFile.getInputStream();
        Workbook wb = WorkbookFactory.create(is);
        Sheet sheet = wb.getSheetAt(0);
        Row row; // 兼容
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        List<String> V_ORLECODELIST = new ArrayList<>(rowNum);
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            V_ORLECODELIST.add(BaseUtils.getCellValue(row.getCell(1)));
        }
        if (perToRoleService.insertPerToRoleBatch(V_PERCODE_FORM, V_ORLECODELIST, V_PERCODE) == 0) {
            return BaseUtils.failed("导入人员角色失败");
        }
        return BaseUtils.success();
    }
}
