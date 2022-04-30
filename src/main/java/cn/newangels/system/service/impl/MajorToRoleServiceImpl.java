package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.MajorToRoleService;
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
 * @author TangLiang
 * @date 2022/02/17 16:48
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MajorToRoleServiceImpl implements MajorToRoleService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectMajorToRole(String V_ORLECODE, String V_MAJORID, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaMajorToRole(false, V_ORLECODE, V_MAJORID);
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
    public int countMajorToRole(String V_ORLECODE, String V_MAJORID) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaMajorToRole(true, V_ORLECODE, V_MAJORID);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertMajorToRoleBatch(List<String> V_ORLECODELIST, String V_MAJORID, String V_PER_EDIT) {
        String sql = "MERGE INTO CON_MAJORTOROLE a USING (SELECT ? as V_ORLECODE, ? as V_MAJORID FROM dual) b" +
                " ON (a.V_ORLECODE = b.V_ORLECODE and a.V_MAJORID = b.V_MAJORID ) WHEN NOT MATCHED THEN INSERT (a.V_ORLECODE, a.V_MAJORID, a.V_PER_EDIT) VALUES (?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_ORLECODELIST.get(i));
                ps.setString(2, V_MAJORID);
                ps.setString(3, V_ORLECODELIST.get(i));
                ps.setString(4, V_MAJORID);
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
    public int deleteMajorToRole(String I_ID) {
        String sql = "delete from CON_MAJORTOROLE where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaMajorToRole(boolean count, String V_ORLECODE, String V_MAJORID) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from CON_MAJORTOROLE C where 1 = 1";
        } else {
            sql = "select C.I_ID, C.V_ORLECODE, C.V_MAJORID, R.V_ORLENAME from CON_MAJORTOROLE C left join BASE_ROLE R on C.V_ORLECODE = R.V_ORLECODE where R.V_STATUS = '1'";
        }
        if (StringUtils.isNotEmpty(V_ORLECODE)) {
            sql += " and C.V_ORLECODE = :V_ORLECODE";
            paramMap.put("V_ORLECODE", V_ORLECODE);
        }
        if (StringUtils.isNotEmpty(V_MAJORID)) {
            sql += " and C.V_MAJORID = :V_MAJORID";
            paramMap.put("V_MAJORID", V_MAJORID);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
