package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.ContractService;
import cn.newangels.system.service.PerToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.newangels.common.base.BaseUtils.DATA;

/**
 * 合同
 *
 * @author: ll
 * @date: 2022/02/16 09:01
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;
    private final SnowflakeIdWorker snowflakeIdWorker;
    private final BaseService baseService;
    private final PerToRoleService perToRoleService;

    /**
     * 加载合同
     */
    @GetMapping("loadContract")
    @Log(title = "合同管理", operateType = "加载合同")
    public Map<String, Object> loadContract(String I_ID) {
        return BaseUtils.loadSuccess(contractService.loadContract(I_ID));
    }

    /**
     * 查询合同
     */
    @GetMapping("selectContract")
    @Log(title = "合同管理", operateType = "查询合同")
    public Map<String, Object> selectContract(String V_SPONSOR, String V_CONTRACTOR, String V_MAJORID, String V_INST_STATUS, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_BEGIN_DATE, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_END_DATE, String V_PROJECTNAME, String V_PERCODE, String V_STAMPSTATUS, Integer current, Integer pageSize) {
        V_END_DATE = BaseUtils.addOneDay(V_END_DATE);
        List<Map<String, Object>> list = contractService.selectContract(V_SPONSOR, V_CONTRACTOR, V_MAJORID, V_INST_STATUS, V_BEGIN_DATE, V_END_DATE, V_PROJECTNAME, V_PERCODE, "1", null, null, V_STAMPSTATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = contractService.countContract(V_SPONSOR, V_CONTRACTOR, V_MAJORID, V_INST_STATUS, V_BEGIN_DATE, V_END_DATE, V_PROJECTNAME, V_PERCODE, "1", null, null, V_STAMPSTATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 查询合同
     */
    @GetMapping("selectContractPermission")
    @Log(title = "合同管理", operateType = "查询合同")
    public Map<String, Object> selectContractPermission(String V_SPONSOR, String V_CONTRACTOR, String V_MAJORID, String V_INST_STATUS, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_BEGIN_DATE, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_END_DATE, String V_PROJECTNAME, String V_PERCODE, String V_DEPTCODE, String V_ORGCODE, Integer current, Integer pageSize) {
        V_END_DATE = BaseUtils.addOneDay(V_END_DATE);
        String permissionType = "";  //权限划分
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> perRoleList = perToRoleService.selectPerToRole(V_PERCODE, null, null, null);
        for (int i = 0; i < perRoleList.size(); i++) {
            if ("drafter".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //起草人 自己起草的
                permissionType = "1";
            } else if ("6".equals(perRoleList.get(i).get("V_ORLECODE").toString()) || "10".equals(perRoleList.get(i).get("V_ORLECODE").toString()) || "11".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //厂矿部门领导 他审批的 + 部门专业所有的
                permissionType = "2";
            } else if ("contractAdministrator".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //合同管理员 自己厂矿所有的
                permissionType = "3";
            } else if ("majorPlanner".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //专业部门计划员 查他部门专业和他审批的
                permissionType = "4";
            } else if ("majorLeader".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //专业部门领导 查他负责所有专业的，也即是他部门所对应的专业
                permissionType = "5";
            } else if ("lawAdviser".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //法务 查所有
                permissionType = "6";
            } else if ("3".equals(perRoleList.get(i).get("V_ORLECODE").toString()) || "8".equals(perRoleList.get(i).get("V_ORLECODE").toString()) || "9".equals(perRoleList.get(i).get("V_ORLECODE").toString())) { //公司级领导 查所有
                permissionType = "7";
            }
            if (!Objects.equals(permissionType, "")) {
                list.addAll(contractService.selectContract(V_SPONSOR, V_CONTRACTOR, V_MAJORID, V_INST_STATUS, V_BEGIN_DATE, V_END_DATE, V_PROJECTNAME, V_PERCODE, permissionType, V_DEPTCODE, V_ORGCODE, null, null, null));
            }
        }
        List<Map<String, Object>> distinctlist = list.stream().distinct().collect(Collectors.toList());
        return BaseUtils.success(list, distinctlist.size());
    }

    /**
     * 新增合同
     */
    @PostMapping("insertContract")
    @Log(title = "合同管理", operateType = "新增合同")
    public Map<String, Object> insertContract(String V_CONTRACTCODE, String V_PROJECTCODE, String V_PROJECTNAME, String V_SPONSORID, String V_SPONSORCODE, String V_SPONSORNAME, String V_OFFICER, String V_CONTRACTORID, String V_CONTRACTORNAME, String V_CREDIT, String V_MONEY, String V_YEAR, Integer I_COPIES, Integer I_REVIEWCOPIES, String V_MAJORID, String V_TEMPLATEID, String V_ACCORDANCE, Date V_STARTDATE, Date V_ENDDATE, MultipartFile[] multipartFiles, String V_PERCODE, String V_JSON, String V_SPONSORABBR, String V_PROJECTTYPE, String V_CONTRACTTYPE, String V_CONTRACTUPID) throws IOException {
        if (contractService.insertContract(String.valueOf(snowflakeIdWorker.nextId()), V_CONTRACTCODE, V_PROJECTCODE, V_PROJECTNAME, V_SPONSORID, V_SPONSORCODE, V_SPONSORNAME, V_OFFICER, V_CONTRACTORID, V_CONTRACTORNAME, V_CREDIT, V_MONEY, V_YEAR, I_COPIES, I_REVIEWCOPIES, V_MAJORID, V_TEMPLATEID, V_ACCORDANCE, V_STARTDATE, V_ENDDATE, multipartFiles, V_PERCODE, V_JSON, V_SPONSORABBR, V_PROJECTTYPE, V_CONTRACTTYPE, V_CONTRACTUPID) == 0) {
            return BaseUtils.failed("新增合同失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改合同
     */
    @PostMapping("updateContract")
    @Log(title = "合同管理", operateType = "修改合同")
    public Map<String, Object> updateContract(String I_ID, String V_CONTRACTCODE, String V_PROJECTCODE, String V_PROJECTNAME, String V_SPONSORID, String V_SPONSORCODE, String V_SPONSORNAME, String V_OFFICER, String V_CONTRACTORID, String V_CONTRACTORNAME, String V_CREDIT, String V_MONEY, String V_YEAR, Integer I_COPIES, Integer I_REVIEWCOPIES, String V_MAJORID, String V_TEMPLATEID, String V_ACCORDANCE, Date V_STARTDATE, Date V_ENDDATE, MultipartFile[] multipartFiles, String V_PERCODE, String V_JSON, String V_SPONSORABBR, String V_PROJECTTYPE, String V_CONTRACTTYPE, String V_CONTRACTUPID) throws IOException {
        if (contractService.updateContract(I_ID, V_CONTRACTCODE, V_PROJECTCODE, V_PROJECTNAME, V_SPONSORID, V_SPONSORCODE, V_SPONSORNAME, V_OFFICER, V_CONTRACTORID, V_CONTRACTORNAME, V_CREDIT, V_MONEY, V_YEAR, I_COPIES, I_REVIEWCOPIES, V_MAJORID, V_TEMPLATEID, V_ACCORDANCE, V_STARTDATE, V_ENDDATE, multipartFiles, V_PERCODE, V_JSON, V_SPONSORABBR, V_PROJECTTYPE, V_CONTRACTTYPE, V_CONTRACTUPID) == 0) {
            return BaseUtils.failed("修改合同失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除合同
     */
    @PostMapping("deleteContract")
    @Log(title = "合同管理", operateType = "删除合同")
    public Map<String, Object> deleteContract(String I_ID, String V_PERCODE) {
        if (contractService.deleteContract(I_ID, V_PERCODE) == 0) {
            return BaseUtils.failed("删除合同失败");
        }
        return BaseUtils.success();
    }

    /**
     * 上报流程
     */
    @PostMapping("startContractProcess")
    @Log(title = "合同管理", operateType = "上报合同流程")
    public Map<String, Object> startContractProcess(String V_PERCODE, String I_ID, String ASSIGNEE_, String PROCESS_DEFINITION_KEY_) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        String processInstanceId = contractService.startContractProcess(I_ID, ASSIGNEE_, PROCESS_DEFINITION_KEY_, operator);
        data.put(DATA, processInstanceId);
        return BaseUtils.success(data);
    }
}
