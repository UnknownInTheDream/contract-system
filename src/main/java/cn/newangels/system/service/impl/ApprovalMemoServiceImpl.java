package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.dto.Task;
import cn.newangels.system.service.ActivitiService;
import cn.newangels.system.service.ApprovalMemoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ApprovalMemoServiceImpl implements ApprovalMemoService {

    private final JdbcTemplate systemJdbcTemplate;
    private final ActivitiService activitiService;
    //@Autowired
    //private EmpService empService;
    //@Autowired
    // private PosiService posiService;

    @Override
    public Map<String, Object> loadApprovalMemo(String TASK_ID_, Map<String, Object> operator) {
        String sql = "select * from CON_APPROVAL_MEMO where TASK_ID_ = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, TASK_ID_);
        if (result.size() == 1) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> selectApprovalMemo(String PROC_INST_ID_, String BIZ_ID_, String ASSIGNEE_CODE_, String APPROVAL_MEMO_STATUS_, Boolean ignoreEmptyDispatch, Integer current, Integer pageSize, Map<String, Object> operator) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaApprovalMemo(false, PROC_INST_ID_, BIZ_ID_, ASSIGNEE_CODE_, APPROVAL_MEMO_STATUS_, ignoreEmptyDispatch, operator);
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        if (current != null && pageSize != null && pageSize > 0) {
            int start = (current - 1) * pageSize + 1;
            int end = current * pageSize;
            sql = "select * from (select FULLTABLE.*, ROWNUM RN from (" + sql + ") FULLTABLE where ROWNUM <= " + end + ") where RN >= " + start;
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(systemJdbcTemplate);
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int countApprovalMemo(String PROC_INST_ID_, String BIZ_ID_, String ASSIGNEE_CODE_, String APPROVAL_MEMO_STATUS_, Boolean ignoreEmptyDispatch, Map<String, Object> operator) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaApprovalMemo(true, PROC_INST_ID_, BIZ_ID_, ASSIGNEE_CODE_, APPROVAL_MEMO_STATUS_, ignoreEmptyDispatch, operator);
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(systemJdbcTemplate);
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    private BaseSqlCriteria buildSqlCriteriaApprovalMemo(boolean count, String PROC_INST_ID_, String BIZ_ID_, String ASSIGNEE_CODE_, String APPROVAL_MEMO_STATUS_, Boolean ignoreEmptyDispatch, Map<String, Object> operator) {
        String sql;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (count) {
            sql = "select count(*) from CON_APPROVAL_MEMO AM where 1 = 1";
        } else {
            sql = "select * from CON_APPROVAL_MEMO AM where 1 = 1";
        }
        if (StringUtils.isNotEmpty(PROC_INST_ID_)) {
            sql += " and AM.PROC_INST_ID_ = :PROC_INST_ID_";
            paramMap.put("PROC_INST_ID_", PROC_INST_ID_);
        }
        if (StringUtils.isNotEmpty(BIZ_ID_)) {
            sql += " and AM.BIZ_ID_ = :BIZ_ID_";
            paramMap.put("BIZ_ID_", BIZ_ID_);
        }
        if (StringUtils.isNotEmpty(ASSIGNEE_CODE_)) {
            sql += " and AM.ASSIGNEE_CODE_ = :ASSIGNEE_CODE_";
            paramMap.put("ASSIGNEE_CODE_", ASSIGNEE_CODE_);
        }
        if (StringUtils.isNotEmpty(APPROVAL_MEMO_STATUS_)) {
            sql += " and AM.APPROVAL_MEMO_STATUS_ = :APPROVAL_MEMO_STATUS_";
            paramMap.put("APPROVAL_MEMO_STATUS_", APPROVAL_MEMO_STATUS_);
        }
        if (ignoreEmptyDispatch != null && ignoreEmptyDispatch == true) {
            sql += " and not((AM.APPROVAL_MEMO_TYPE_ = 1 or AM.APPROVAL_MEMO_TYPE_ = 2) and AM.APPROVAL_MEMO_ is null)";
        }
        if (!count) {
            sql += " order by AM.CREATION_DATE_";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }

    @Override
    public List<Map<String, Object>> selectOwnApprovalMemo(String ASSIGNEE_CODE_, String CODE_, String TITLE_, String APPROVAL_MEMO_STATUS_, Integer current, Integer pageSize, Map<String, Object> operator) {
        List<Object> result = buildOwnSql(false, ASSIGNEE_CODE_, CODE_, TITLE_, APPROVAL_MEMO_STATUS_, operator);
        String sql = (String) result.get(0);
        Map<String, Object> paramMap = (Map<String, Object>) result.get(1);
        if (current != null && pageSize != null && pageSize > 0) {
            int start = (current - 1) * pageSize + 1;
            int end = current * pageSize;
            sql = "select * from (select FULLTABLE.*, ROWNUM RN from (" + sql + ") FULLTABLE where ROWNUM <= " + end + ") where RN >= " + start;
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(systemJdbcTemplate);
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @Override
    public int countOwnApprovalMemo(String ASSIGNEE_CODE_, String CODE_, String TITLE_, String APPROVAL_MEMO_STATUS_, Map<String, Object> operator) {
        List<Object> result = buildOwnSql(true, ASSIGNEE_CODE_, CODE_, TITLE_, APPROVAL_MEMO_STATUS_, operator);
        String sql = (String) result.get(0);
        Map<String, Object> paramMap = (Map<String, Object>) result.get(1);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(systemJdbcTemplate);
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    private List<Object> buildOwnSql(boolean count, String ASSIGNEE_CODE_, String V_PROJECTCODE, String V_PROJECTNAME, String APPROVAL_MEMO_STATUS_, Map<String, Object> operator) {
        String sql;
        if (count) {
            sql = "select count(*) from CON_APPROVAL_MEMO AM inner join CON_CONTRACT F on F.I_ID = AM.BIZ_ID_ where AM.ASSIGNEE_CODE_ = :ASSIGNEE_CODE_";
        } else {
            sql = "select AM.*, F.V_CONTRACTCODE, F.V_PROJECTCODE, F.V_PROJECTNAME from CON_APPROVAL_MEMO AM inner join CON_CONTRACT F on AM.BIZ_ID_ = F.I_ID where AM.ASSIGNEE_CODE_ = :ASSIGNEE_CODE_";
        }
        Map<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("ASSIGNEE_CODE_", ASSIGNEE_CODE_);
        if (StringUtils.isNotEmpty(V_PROJECTCODE)) {
            sql += " and V_PROJECTCODE like '%' || :V_PROJECTCODE || '%'";
            paramMap.put("V_PROJECTCODE", V_PROJECTCODE);
        }
        if (StringUtils.isNotEmpty(V_PROJECTNAME)) {
            sql += " and V_PROJECTNAME like '%' || :V_PROJECTNAME || '%'";
            paramMap.put("V_PROJECTNAME", V_PROJECTNAME);
        }
        if (APPROVAL_MEMO_STATUS_ != null) {
            sql += " and APPROVAL_MEMO_STATUS_ = :APPROVAL_MEMO_STATUS_";
            paramMap.put("APPROVAL_MEMO_STATUS_", APPROVAL_MEMO_STATUS_);
        }
        if (!count) {
            sql += " order by AM.CREATION_DATE_ desc";
        }
        List<Object> result = new ArrayList<Object>();
        result.add(sql);
        result.add(paramMap);
        return result;
    }

    @Override
    public int insertApprovalMemo(String TASK_ID_, String PARENT_TASK_ID_, String EXECUTION_ID_, String PROC_INST_ID_, String TASK_NAME_, String BIZ_ID_, String ASSIGNEE_CODE_, String ASSIGNEE_NAME_, String ROLE_ID_, String ROLE_NAME_, String ORG_CODE_, String ORG_NAME_, String COM_CODE_, String COM_NAME_, String EMAIL_, String DELEGATE_CODE_, String DELEGATE_NAME_, Date CREATION_DATE_, Date DUE_DATE_, Integer APPROVAL_MEMO_TYPE_, String APPROVAL_MEMO_, String APPROVAL_MEMO_STATUS_, Map<String, Object> operator) {
        String sql = "select count(*) from CON_APPROVAL_MEMO where PROC_INST_ID_ = ?";
        Integer count = systemJdbcTemplate.queryForObject(sql, new Object[]{PROC_INST_ID_}, Integer.class);
        // 第一步不发即时通
        if (count == 0) {
            //ToDo
        }
        sql = "insert into CON_APPROVAL_MEMO(TASK_ID_, PARENT_TASK_ID_, EXECUTION_ID_, PROC_INST_ID_, TASK_NAME_, BIZ_ID_, ASSIGNEE_CODE_, ASSIGNEE_NAME_, ROLE_ID_, ROLE_NAME_, ORG_CODE_, ORG_NAME_, COM_CODE_, COM_NAME_, EMAIL_, DELEGATE_CODE_, DELEGATE_NAME_, CREATION_DATE_, DUE_DATE_, APPROVAL_MEMO_TYPE_, APPROVAL_MEMO_, APPROVAL_MEMO_STATUS_) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, TASK_ID_, PARENT_TASK_ID_, EXECUTION_ID_, PROC_INST_ID_, TASK_NAME_, BIZ_ID_, ASSIGNEE_CODE_, ASSIGNEE_NAME_, ROLE_ID_, ROLE_NAME_, ORG_CODE_, ORG_NAME_, COM_CODE_, COM_NAME_, EMAIL_, DELEGATE_CODE_, DELEGATE_NAME_, CREATION_DATE_, DUE_DATE_, APPROVAL_MEMO_TYPE_, APPROVAL_MEMO_, APPROVAL_MEMO_STATUS_);
    }

    @Override
    public int insertApprovalMemo(List<String> TASK_ID_LIST, String PARENT_TASK_ID_, String BIZ_ID_, List<String> ASSIGNEE_CODE_LIST, Date CREATION_DATE_, Date DUE_DATE_, Integer APPROVAL_MEMO_TYPE_, Map<String, Object> operator) {
        String TASK_ID_;
        String ASSIGNEE_CODE_;
        String ROLE_ID_;
        Task task;
        Map<String, Object> person;
        Map<String, Object> posi;
        String assigneeString;
        for (int i = 0; i < TASK_ID_LIST.size(); i++) {
            TASK_ID_ = TASK_ID_LIST.get(i);
            assigneeString = ASSIGNEE_CODE_LIST.get(i);
            ASSIGNEE_CODE_ = assigneeString.substring(assigneeString.indexOf("[") + 1, assigneeString.indexOf("]"));
            ROLE_ID_ = assigneeString.substring(assigneeString.lastIndexOf("[") + 1, assigneeString.lastIndexOf("]"));
            task = activitiService.loadTask(TASK_ID_, operator);
            // person = empService.loadEmp(ASSIGNEE_CODE_);  //TODO 查询人员
            // posi = posiService.loadPosi(ROLE_ID_);  //TODO 查询岗位
            //TODO 新增审批意见
            //insertApprovalMemo(TASK_ID_, PARENT_TASK_ID_, task.getExecutionId(), task.getProcessInstanceId(), task.getTaskName(), BIZ_ID_, ASSIGNEE_CODE_, (String) person.get("AC_AC_VCH_USERNAME"), (String) posi.get("ROLE_ID_"), (String) posi.get("ROLE_NAME_"), (String) person.get("OM_OR_UID_ID"), (String) person.get("OM_OR_NVC_NAME"), null, null, "", null, null, CREATION_DATE_, DUE_DATE_, APPROVAL_MEMO_TYPE_, null, "0", operator);
        }
        return TASK_ID_LIST.size();
    }

    @Override
    public int updateApprovalMemo(String TASK_ID_, String APPROVAL_MEMO_, Integer APPROVAL_MEMO_STATUS_, Map<String, Object> operator) {
        String sql = "update CON_APPROVAL_MEMO set APPROVAL_DATE_ = ?, APPROVAL_MEMO_ = ?, APPROVAL_MEMO_STATUS_ = ? where TASK_ID_ = ?";
        int count = systemJdbcTemplate.update(sql, new Date(), APPROVAL_MEMO_, APPROVAL_MEMO_STATUS_, TASK_ID_);
        return count;
    }

    @Override
    public int deleteApprovalMemo(String TASK_ID_, Map<String, Object> operator) {
        String sql = "delete from CON_APPROVAL_MEMO where TASK_ID_ = ?";
        return systemJdbcTemplate.update(sql, TASK_ID_);
    }

    @Override
    public int deleteApprovalMemoByFormId(String BIZ_ID_, Map<String, Object> operator) {
        String sql = "delete from CON_APPROVAL_MEMO where BIZ_ID_ = ?";
        return systemJdbcTemplate.update(sql, BIZ_ID_);
    }

    @Override
    public int withdrawToTask(String TASK_ID_, String DELEGATE_CODE_, String DELEGATE_NAME_, String APPROVAL_MEMO_, Map<String, Object> operator) {
        Map<String, Object> approvalMemo = loadApprovalMemo(TASK_ID_, operator);
        String PROC_INST_ID_ = (String) approvalMemo.get("PROC_INST_ID_");
        Date CREATION_DATE_ = (Date) approvalMemo.get("CREATION_DATE_");
        String sql = "delete from CON_APPROVAL_MEMO where PROC_INST_ID_ = ? and CREATION_DATE_ > ? and CREATION_DATE_ <> (select max(CREATION_DATE_) from CON_APPROVAL_MEMO where PROC_INST_ID_ = ?)";
        return systemJdbcTemplate.update(sql, PROC_INST_ID_, CREATION_DATE_, PROC_INST_ID_);
    }

    @Override
    public boolean hasApprove(String BIZ_ID_, String EMP_CODE_) {
        String sql = "select count(*) from CON_APPROVAL_MEMO where BIZ_ID_ = ? and ASSIGNEE_CODE_ = ? and APPROVAL_MEMO_STATUS_ != 0";
        if (systemJdbcTemplate.queryForObject(sql, new Object[]{BIZ_ID_, EMP_CODE_}, Integer.class) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Integer getApprovalType(String taskId) {
        try {
            String sql = "select APPROVAL_MEMO_TYPE_ from CON_APPROVAL_MEMO where TASK_ID_=?";
            return systemJdbcTemplate.queryForObject(sql, new Object[]{taskId}, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
