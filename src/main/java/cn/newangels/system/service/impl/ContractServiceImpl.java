package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.common.exception.UnExpectedResultException;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.dto.ProcessInstance;
import cn.newangels.system.dto.Task;
import cn.newangels.system.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author ll
 * @date 2022/02/16 09:01
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ApprovalMemoService approvalMemoService;
    private final ApproveService approveService;
    private final ContractRiderService contractRiderService;
    private final ContractFileService contractFileService;
    private final SnowflakeIdWorker snowflakeIdWorker;
    private final ThreadPoolTaskExecutor applicationTaskExecutor;

    @Override
    public Map<String, Object> loadContract(String I_ID) {
        String sql = "select c.*, t.V_NAME as V_TEMPLATE, m.v_name as V_MAJOR from CON_CONTRACT c left join CON_TEMPLATE t on c.V_TEMPLATEID = t.I_ID left join CON_MAJOR m on m.i_id = c.v_majorid where c.I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectContract(String V_SPONSORNAME, String V_CONTRACTORNAME, String V_MAJORID, String V_INST_STATUS, Date V_BEGIN_DATE, Date V_END_DATE, String V_PROJECTNAME, String V_PER_EDIT, String permissionType, String V_DEPTCODE, String V_ORGCODE, String V_STAMPSTATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContract(false, V_SPONSORNAME, V_CONTRACTORNAME, V_MAJORID, V_INST_STATUS, V_BEGIN_DATE, V_END_DATE, V_PROJECTNAME, V_PER_EDIT, permissionType, V_DEPTCODE, V_ORGCODE,V_STAMPSTATUS);
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        if (current != null && pageSize != null && pageSize > 0) {
            int start = (current - 1) * pageSize + 1;
            int end = current * pageSize;
            sql = "select * from (select FULLTABLE.*, ROWNUM RN from (" + sql + ") FULLTABLE where ROWNUM <= " + end + ") where RN >= " + start;
        }
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @Override
    public int countContract(String V_SPONSORNAME, String V_CONTRACTORNAME, String V_MAJORID, String V_INST_STATUS, Date V_BEGIN_DATE, Date V_END_DATE, String V_PROJECTNAME, String V_PER_EDIT, String permissionType, String V_DEPTCODE, String V_ORGCODE, String V_STAMPSTATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContract(true, V_SPONSORNAME, V_CONTRACTORNAME, V_MAJORID, V_INST_STATUS, V_BEGIN_DATE, V_END_DATE, V_PROJECTNAME, V_PER_EDIT, permissionType, V_DEPTCODE, V_ORGCODE,V_STAMPSTATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertContract(String I_ID, String V_CONTRACTCODE, String V_PROJECTCODE, String V_PROJECTNAME, String V_SPONSORID, String V_SPONSORCODE, String V_SPONSORNAME, String V_OFFICER, String V_CONTRACTORID, String V_CONTRACTORNAME, String V_CREDIT, String V_MONEY, String V_YEAR, Integer I_COPIES, Integer I_REVIEWCOPIES, String V_MAJORID, String V_TEMPLATEID, String V_ACCORDANCE, Date V_STARTDATE, Date V_ENDDATE, MultipartFile[] multipartFiles, String V_PER_EDIT, String V_JSON, String V_SPONSORABBR, String V_PROJECTTYPE, String V_CONTRACTTYPE, String V_CONTRACTUPID) throws IOException {
        String sql = "insert into CON_CONTRACT (I_ID, V_CONTRACTCODE, V_PROJECTCODE, V_PROJECTNAME, V_SPONSORID, V_SPONSORCODE, V_SPONSORNAME, V_OFFICER, V_CONTRACTORID, V_CONTRACTORNAME, V_CREDIT, V_MONEY, V_YEAR, I_COPIES, I_REVIEWCOPIES, V_MAJORID, V_TEMPLATEID, V_ACCORDANCE, V_STARTDATE, V_ENDDATE, V_PER_EDIT, V_SPONSORABBR, V_PROJECTTYPE, V_CONTRACTTYPE, V_CONTRACTUPID) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int count = systemJdbcTemplate.update(sql, I_ID, V_CONTRACTCODE, V_PROJECTCODE, V_PROJECTNAME, V_SPONSORID, V_SPONSORCODE, V_SPONSORNAME, V_OFFICER, V_CONTRACTORID, V_CONTRACTORNAME, V_CREDIT, new BigDecimal(V_MONEY), V_YEAR, I_COPIES, I_REVIEWCOPIES, V_MAJORID, V_TEMPLATEID, V_ACCORDANCE, V_STARTDATE, V_ENDDATE, V_PER_EDIT, V_SPONSORABBR, V_PROJECTTYPE, V_CONTRACTTYPE, V_CONTRACTUPID);
        if (count == 0) {
            throw new UnExpectedResultException("新增合同失败");
        }
        for (MultipartFile multipartFile : multipartFiles) {
            contractRiderService.insertContractRider(String.valueOf(snowflakeIdWorker.nextId()), I_ID, multipartFile, V_PER_EDIT);
        }
        contractFileService.insertContractFileByTemplate(String.valueOf(snowflakeIdWorker.nextId()), I_ID, V_TEMPLATEID, V_PER_EDIT, V_JSON);
        return count;
    }

    @Override
    public int updateContract(String I_ID, String V_CONTRACTCODE, String V_PROJECTCODE, String V_PROJECTNAME, String V_SPONSORID, String V_SPONSORCODE, String V_SPONSORNAME, String V_OFFICER, String V_CONTRACTORID, String V_CONTRACTORNAME, String V_CREDIT, String V_MONEY, String V_YEAR, Integer I_COPIES, Integer I_REVIEWCOPIES, String V_MAJORID, String V_TEMPLATEID, String V_ACCORDANCE, Date V_STARTDATE, Date V_ENDDATE, MultipartFile[] multipartFiles, String V_PER_EDIT, String V_JSON, String V_SPONSORABBR, String V_PROJECTTYPE, String V_CONTRACTTYPE, String V_CONTRACTUPID) throws IOException {
        String sql = "update CON_CONTRACT set V_CONTRACTCODE = ?, V_PROJECTCODE = ?, V_PROJECTNAME = ?, V_SPONSORID = ?, V_SPONSORCODE = ?, V_SPONSORNAME = ?, V_OFFICER = ?, V_CONTRACTORID = ?, V_CONTRACTORNAME = ?, V_CREDIT = ?, V_MONEY = ?, V_YEAR = ?, I_COPIES = ?, I_REVIEWCOPIES = ?, V_MAJORID = ?, V_TEMPLATEID = ?, V_ACCORDANCE = ?, V_STARTDATE = ?, V_ENDDATE = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ?, V_SPONSORABBR = ?, V_PROJECTTYPE = ?, V_CONTRACTTYPE = ?, V_CONTRACTUPID = ? where I_ID = ?";
        int count = systemJdbcTemplate.update(sql, V_CONTRACTCODE, V_PROJECTCODE, V_PROJECTNAME, V_SPONSORID, V_SPONSORCODE, V_SPONSORNAME, V_OFFICER, V_CONTRACTORID, V_CONTRACTORNAME, V_CREDIT, new BigDecimal(V_MONEY), V_YEAR, I_COPIES, I_REVIEWCOPIES, V_MAJORID, V_TEMPLATEID, V_ACCORDANCE, V_STARTDATE, V_ENDDATE, V_PER_EDIT, V_SPONSORABBR, V_PROJECTTYPE, V_CONTRACTTYPE, V_CONTRACTUPID, I_ID);
        if (count == 0) {
            throw new UnExpectedResultException("修改合同失败");
        }
        for (MultipartFile multipartFile : multipartFiles) {
            contractRiderService.insertContractRider(String.valueOf(snowflakeIdWorker.nextId()), I_ID, multipartFile, V_PER_EDIT);
        }
        Map<String, Object> map = contractFileService.loadContractFile(I_ID);
        //json串发生变化时
        //if (!V_JSON.equals(BaseUtils.valueOf(map.get("V_JSON")))) {
            contractFileService.deleteContractFile(map.get("I_ID").toString(), map.get("V_FILEPATH").toString(), V_PER_EDIT);
            contractFileService.insertContractFileByTemplate(String.valueOf(snowflakeIdWorker.nextId()), I_ID, V_TEMPLATEID, V_PER_EDIT, V_JSON);
        // }
        return count;
    }

    @Override
    public int deleteContract(String I_ID, String V_PERCODE) {
        String sql = "delete from CON_CONTRACT where I_ID = ?";
        int count = systemJdbcTemplate.update(sql, I_ID);
        contractFileService.deleteContractFileByGuid(I_ID, V_PERCODE);
        contractFileService.deleteContractFileExtraByGuid(I_ID, V_PERCODE);
        contractRiderService.deleteContractRiderByGuid(I_ID, V_PERCODE);
        return count;
    }

    private BaseSqlCriteria buildSqlCriteriaContract(boolean count, String V_SPONSORNAME, String V_CONTRACTORNAME, String V_MAJORID, String V_INST_STATUS, Date V_BEGIN_DATE, Date V_END_DATE, String V_PROJECTNAME, String V_PER_EDIT, String permissionType, String V_DEPTCODE, String V_ORGCODE, String V_STAMPSTATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(16);
        if (count) {
            sql = "select count(*) from CON_CONTRACT c left join CON_MAJOR m on m.i_id = c.v_majorid where 1 = 1";
        } else {
            sql = "select c.*, m.v_name as V_MAJOR, case when c.v_enddate > (select delaydate from con_delaydate) then 0 else 1 end as DELAY from CON_CONTRACT c left join CON_MAJOR m on m.i_id = c.v_majorid where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_SPONSORNAME)) {
            sql += " and c.V_SPONSORNAME = :V_SPONSORNAME";
            paramMap.put("V_SPONSORNAME", V_SPONSORNAME);
        }
        if (StringUtils.isNotEmpty(V_CONTRACTORNAME)) {
            sql += " and c.V_CONTRACTORNAME = :V_CONTRACTORNAME";
            paramMap.put("V_CONTRACTORNAME", V_CONTRACTORNAME);
        }
        if (StringUtils.isNotEmpty(V_MAJORID)) {
            sql += " and c.V_MAJORID = :V_MAJORID";
            paramMap.put("V_MAJORID", V_MAJORID);
        }
        if (StringUtils.isNotEmpty(V_INST_STATUS)) {
            sql += " and c.V_INST_STATUS = :V_INST_STATUS";
            paramMap.put("V_INST_STATUS", V_INST_STATUS);
        }
        if (V_BEGIN_DATE != null) {
            sql += " and c.D_DATE_CREATE >= :V_BEGIN_DATE";
            paramMap.put("V_BEGIN_DATE", V_BEGIN_DATE);
        }
        if (V_END_DATE != null) {
            sql += " and c.D_DATE_CREATE <= :V_END_DATE";
            paramMap.put("V_END_DATE", V_END_DATE);
        }
        if (StringUtils.isNotEmpty(V_PROJECTNAME)) {
            sql += " and V_PROJECTNAME like '%' || :V_PROJECTNAME || '%'";
            paramMap.put("V_PROJECTNAME", V_PROJECTNAME);
        }
        if (!"6".equals(permissionType) && !"7".equals(permissionType)) {
            if (StringUtils.isNotEmpty(V_PER_EDIT) && "1".equals(permissionType)) {
                sql += " and c.V_PER_EDIT = :V_PER_EDIT";
                paramMap.put("V_PER_EDIT", V_PER_EDIT);
            } else if ("2".equals(permissionType)) {
                sql += " and c.I_ID in (select BIZ_ID_ from CON_APPROVAL_MEMO  where ASSIGNEE_CODE_= :V_PER_EDIT) or c.V_MAJORID in (select I_ID from CON_MAJOR  where V_DEPTCODE = :V_DEPTCODE)";
                paramMap.put("V_PER_EDIT", V_PER_EDIT);
                paramMap.put("V_DEPTCODE", V_DEPTCODE);
            } else if ("3".equals(permissionType)) {
                sql += " and c.V_MAJORID in (select I_ID from CON_MAJOR  where V_DEPTCODE in (select V_DEPTCODE from BASE_DEPT  start with V_DEPTCODE = :V_ORGCODE connect by prior V_DEPTCODE = V_DEPTCODE_UP))";
                paramMap.put("V_ORGCODE", V_ORGCODE);
            } else if ("4".equals(permissionType)) {
                sql += " and c.I_ID in (select BIZ_ID_ from CON_APPROVAL_MEMO  where ASSIGNEE_CODE_= :V_PER_EDIT) or m.V_NAME in (select m.V_NAME from CON_MAJOR m left join CON_MAJORTOROLE r on r.V_MAJORID = m.I_ID  where m.V_DEPTCODE in (select V_DEPTCODE from BASE_DEPT  start with V_DEPTCODE = :V_ORGCODE connect by prior V_DEPTCODE = V_DEPTCODE_UP) and r.V_ORLECODE ='majorPlanner')";
                paramMap.put("V_PER_EDIT", V_PER_EDIT);
                paramMap.put("V_ORGCODE", V_ORGCODE);
            } else if ("5".equals(permissionType)) {
                sql += " and m.V_NAME in (select m.V_NAME from CON_MAJOR m where m.V_DEPTCODE in (select V_DEPTCODE from BASE_DEPT start with V_DEPTCODE = :V_ORGCODE connect by prior V_DEPTCODE = V_DEPTCODE_UP))";
                paramMap.put("V_ORGCODE", V_ORGCODE);
            }
        }
        if (StringUtils.isNotEmpty(V_STAMPSTATUS)) {
            sql += " and c.V_STAMPSTATUS = :V_STAMPSTATUS";
            paramMap.put("V_STAMPSTATUS", V_STAMPSTATUS);
        }
        if (!count) {
            sql += " order by c.D_DATE_CREATE desc";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }

    @Override
    public List<Map<String, Object>> selectContractInTask(String I_ID, Date V_BEGIN_DATE, Date V_END_DATE) {
        Map<String, Object> paramMap = new HashMap<>(4);
        String sql = "select c.*, m.v_name as V_MAJOR from CON_CONTRACT c left join CON_MAJOR m on m.i_id = c.v_majorid where c.I_ID = :I_ID";
        paramMap.put("I_ID", I_ID);
        if (V_BEGIN_DATE != null) {
            sql += " and c.D_DATE_CREATE >= :V_BEGIN_DATE";
            paramMap.put("V_BEGIN_DATE", V_BEGIN_DATE);
        }
        if (V_END_DATE != null) {
            sql += " and c.D_DATE_CREATE <= :V_END_DATE";
            paramMap.put("V_END_DATE", V_END_DATE);
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(systemJdbcTemplate);
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @Override
    public String startContractProcess(String I_ID, String ASSIGNEE_, String PROCESS_DEFINITION_KEY_, Map<String, Object> operator) {
        // 新建流程
        Map<String, Object> processVariables = new HashMap<>(16);
        Map<String, Object> contract = loadContract(I_ID);
        //查询条件
        processVariables.put("V_CONTRACTCODE", contract.get("V_CONTRACTCODE").toString());
        processVariables.put("V_SPONSORNAME", contract.get("V_SPONSORNAME").toString());
        processVariables.put("V_CONTRACTORNAME", contract.get("V_CONTRACTORNAME").toString());
        processVariables.put("V_MAJORID", contract.get("V_MAJORID").toString());

        //调用公用方法
        Map<String, String> startApproval = approveService.startApproval(I_ID, ASSIGNEE_, PROCESS_DEFINITION_KEY_, processVariables, operator);
        String processInstanceId = startApproval.get("processInstanceId");
        //更新起草人审批意见
        approvalMemoService.updateApprovalMemo(startApproval.get("taskId"), "起草", 1, operator);
        //更新业务表流程状态 0待审批1审批通过2审批未通过
        updateContractApprovalStatus(I_ID, 0, processInstanceId);
        return processInstanceId;
    }

    @Override
    public int updateContractApprovalStatus(String I_ID, Integer V_INST_STATUS, String V_PROCESS_INSTANCE_ID) {
        String sql = "update CON_CONTRACT set V_INST_STATUS = ?, V_PROCESS_INSTANCE_ID = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, new Object[]{V_INST_STATUS, V_PROCESS_INSTANCE_ID, I_ID});
    }

    @Override
    public int completeContractInTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, ArrayList<String> ASSIGNEE_LIST, String APPROVAL_MEMO_, Map<String, Object> operator, Task task) {
        ProcessInstance processInstance = approveService.completeTask(TASK_ID_, APPROVAL_, ASSIGNEE_, APPROVAL_MEMO_, operator, task);
        //审批意见状态 0待审批1审批通过2审批未通过4撤审
        int approvalMemoStatus = 1;
        if ("reject".equals(APPROVAL_)) {
            updateContractApprovalStatus(task.getBusinessKey(), 2, task.getProcessInstanceId());
            approvalMemoStatus = 2;
        } else {
            updateContractApprovalStatus(task.getBusinessKey(), 0, task.getProcessInstanceId());
        }
        if (ProcessInstance.STATUS_FINISHED.equals(processInstance.getStatus())) {//流程结束
            //更新业务表流程状态
            updateContractApprovalStatus(task.getBusinessKey(), 1, task.getProcessInstanceId());
        }
        //更新起草人审批意见
        return approvalMemoService.updateApprovalMemo(TASK_ID_, APPROVAL_MEMO_, approvalMemoStatus, operator);
    }

    @Override
    public int withdrawContractInTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, ArrayList<String> ASSIGNEE_LIST, String APPROVAL_MEMO_, Map<String, Object> operator, Task task) {
        approveService.withDrawContractTask(TASK_ID_, APPROVAL_, ASSIGNEE_, APPROVAL_MEMO_, operator, task);
        //审批意见状态 0待审批1审批通过2审批未通过4撤审
        if ("rollback".equals(APPROVAL_)) {
            updateContractApprovalStatus(task.getBusinessKey(), 4, task.getProcessInstanceId());
        }
        //更新起草人审批意见
        return approvalMemoService.updateApprovalMemo(TASK_ID_, APPROVAL_MEMO_, 4, operator);
    }

    @Override
    public int updateContractStampStatus(String I_ID, Integer V_STAMPSTATUS) {
        String sql = "update CON_CONTRACT set V_STAMPSTATUS = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, new Object[]{V_STAMPSTATUS, I_ID});
    }
}
