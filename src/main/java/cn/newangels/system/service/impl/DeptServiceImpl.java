package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TangLiang
 * @date 2022/01/27 09:21
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadDept(String I_ID) {
        String sql = "select * from BASE_DEPT where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectDept(String V_DEPTCODE, String V_DEPTNAME, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_STATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaDept(false, V_DEPTCODE, V_DEPTNAME, V_DEPTTYPE, V_DEPTCODE_UP, V_STATUS);
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
    public int countDept(String V_DEPTCODE, String V_DEPTNAME, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_STATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaDept(true, V_DEPTCODE, V_DEPTNAME, V_DEPTTYPE, V_DEPTCODE_UP, V_STATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertDept(String I_ID, String V_DEPTCODE, String V_DEPTNAME, String V_DEPTNAME_FULL, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_DLFR, String V_PRODUCE, String V_DQ, String V_PER_EDIT) {
        String sql = "insert into BASE_DEPT (I_ID, V_DEPTCODE, V_DEPTNAME, V_DEPTNAME_FULL, V_DEPTTYPE, V_DEPTCODE_UP, V_DLFR, V_PRODUCE, V_DQ, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_DEPTCODE, V_DEPTNAME, V_DEPTNAME_FULL, V_DEPTTYPE, V_DEPTCODE_UP, V_DLFR, V_PRODUCE, V_DQ, V_PER_EDIT);
    }

    @Override
    public int updateDept(String I_ID, String V_DEPTCODE, String V_DEPTNAME, String V_DEPTNAME_FULL, String V_DEPTTYPE, String V_DLFR, String V_PRODUCE, String V_DQ, String V_PER_EDIT) {
        String sql = "update BASE_DEPT set V_DEPTCODE = ?, V_DEPTNAME = ?, V_DEPTNAME_FULL = ?, V_DEPTTYPE = ?, V_DLFR = ?, V_PRODUCE = ?, V_DQ = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_DEPTCODE, V_DEPTNAME, V_DEPTNAME_FULL, V_DEPTTYPE, V_DLFR, V_PRODUCE, V_DQ, V_PER_EDIT, I_ID);
    }

    @Override
    public int updateDeptStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        String sql = "update BASE_DEPT set V_STATUS = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PERCODE, I_ID);
    }

    @Override
    public int deleteDept(String I_ID) {
        String sql = "delete from BASE_DEPT where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaDept(boolean count, String V_DEPTCODE, String V_DEPTNAME, String V_DEPTTYPE, String V_DEPTCODE_UP, String V_STATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(8);
        if (count) {
            sql = "select count(*) from BASE_DEPT where 1 = 1";
        } else {
            sql = "select * from BASE_DEPT where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_STATUS)) {
            sql += " and V_STATUS = :V_STATUS";
            paramMap.put("V_STATUS", V_STATUS);
        }
        if (StringUtils.isNotEmpty(V_DEPTTYPE)) {
            sql += " and V_DEPTTYPE = :V_DEPTTYPE";
            paramMap.put("V_DEPTTYPE", V_DEPTTYPE);
        }
        if (StringUtils.isNotEmpty(V_DEPTNAME)) {
            sql += " and V_DEPTNAME like '%' || :V_DEPTNAME || '%'";
            paramMap.put("V_DEPTNAME", V_DEPTNAME);
        }
        if (StringUtils.isNotEmpty(V_DEPTCODE)) {
            sql += " and V_DEPTCODE = :V_DEPTCODE";
            paramMap.put("V_DEPTCODE", V_DEPTCODE);
        }
        if (StringUtils.isNotEmpty(V_DEPTCODE_UP)) {
            sql += " and V_DEPTCODE_UP = :V_DEPTCODE_UP";
            paramMap.put("V_DEPTCODE_UP", V_DEPTCODE_UP);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
