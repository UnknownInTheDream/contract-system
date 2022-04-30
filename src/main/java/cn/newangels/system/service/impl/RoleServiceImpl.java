package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.RoleService;
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
 * @author: JinHongKe
 * @date: 2022/2/10
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadRole(String I_ID) {
        String sql = "select * from BASE_ROLE where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectRole(String V_ORLECODE, String V_ORLENAME, String V_STATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaRole(false, V_ORLECODE, V_ORLENAME, V_STATUS);
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
    public int countRole(String V_ORLECODE, String V_ORLENAME, String V_STATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaRole(true, V_ORLECODE, V_ORLENAME, V_STATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertRole(String I_ID, String V_ORLECODE, String V_ORLENAME, String V_PER_EDIT) {
        String sql = "insert into BASE_ROLE (I_ID, V_ORLECODE, V_ORLENAME, V_PER_EDIT) values(?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_ORLECODE, V_ORLENAME, V_PER_EDIT);
    }

    @Override
    public int updateRole(String I_ID, String V_ORLENAME, String V_PER_EDIT) {
        String sql = "update BASE_ROLE set V_ORLENAME = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_ORLENAME, V_PER_EDIT, I_ID);
    }

    @Override
    public int updateRoleStatus(String I_ID, String V_STATUS, String V_PER_EDIT) {
        String sql = "update BASE_ROLE set V_STATUS = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PER_EDIT, I_ID);
    }

    @Override
    public int deleteRole(String I_ID) {
        String sql = "delete from BASE_ROLE where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaRole(boolean count, String V_ORLECODE, String V_ORLENAME, String V_STATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select COUNT(*) from BASE_ROLE where 1 = 1";
        } else {
            sql = "select * from BASE_ROLE where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_ORLENAME)) {
            sql += " and V_ORLENAME like '%' || :V_ORLENAME || '%'";
            paramMap.put("V_ORLENAME", V_ORLENAME);
        }
        if (StringUtils.isNotEmpty(V_STATUS)) {
            sql += " and V_STATUS = :V_STATUS ";
            paramMap.put("V_STATUS", V_STATUS);
        }
        if (StringUtils.isNotEmpty(V_ORLECODE)) {
            sql += " and V_ORLECODE = :V_ORLECODE ";
            paramMap.put("V_ORLECODE", V_ORLECODE);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
