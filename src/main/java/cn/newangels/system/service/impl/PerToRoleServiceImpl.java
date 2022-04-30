package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PerToRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: TangLiang
 * @date: 2022/2/10 10:19
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PerToRoleServiceImpl implements PerToRoleService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectPerToRole(String V_PERCODE, String V_ORLECODE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerToRole(false, V_PERCODE, V_ORLECODE);
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
    public int countPerToRole(String V_PERCODE, String V_ORLECODE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerToRole(true, V_PERCODE, V_ORLECODE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPerToRoleBatch(String V_PERCODE, List<String> V_ORLECODELIST, String V_PER_EDIT) {
        String sql = "MERGE INTO BASE_PERTOROLE a USING (SELECT ? as V_PERCODE, ? as V_ORLECODE FROM dual) b" +
                " ON (a.V_PERCODE = b.V_PERCODE and a.V_ORLECODE = b.V_ORLECODE ) WHEN NOT MATCHED THEN INSERT (a.V_PERCODE, a.V_ORLECODE, a.V_PER_EDIT) VALUES (?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_PERCODE);
                ps.setString(2, V_ORLECODELIST.get(i));
                ps.setString(3, V_PERCODE);
                ps.setString(4, V_ORLECODELIST.get(i));
                ps.setString(5, V_PER_EDIT);
            }

            @Override
            public int getBatchSize() {
                return V_ORLECODELIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int deletePerToRole(String I_ID) {
        String sql = "delete from BASE_PERTOROLE where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaPerToRole(boolean count, String V_PERCODE, String V_ORLECODE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from BASE_PERTOROLE B left join BASE_ROLE P on B.V_ORLECODE = P.V_ORLECODE where P.V_STATUS = '1'";
        } else {
            sql = "select B.I_ID, B.V_ORLECODE, P.V_ORLENAME from BASE_PERTOROLE B left join BASE_ROLE P on B.V_ORLECODE = P.V_ORLECODE where P.V_STATUS = '1'";
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and B.V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        if (StringUtils.isNotEmpty(V_ORLECODE)) {
            sql += " and B.V_ORLECODE = :V_ORLECODE";
            paramMap.put("V_ORLECODE", V_ORLECODE);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
