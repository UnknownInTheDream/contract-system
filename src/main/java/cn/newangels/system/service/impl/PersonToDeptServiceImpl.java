package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PersonToDeptService;
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
 * @date 2022/02/08 09:50
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PersonToDeptServiceImpl implements PersonToDeptService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectPersonToDept(String V_PERCODE, String V_ORGCODE, String V_DEPTCODE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPersonToDept(false, V_PERCODE, V_ORGCODE, V_DEPTCODE);
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
    public int countPersonToDept(String V_PERCODE, String V_ORGCODE, String V_DEPTCODE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPersonToDept(true, V_PERCODE, V_ORGCODE, V_DEPTCODE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPersonToDeptBatch(List<String> V_PERCODELIST, String V_ORGCODE, String V_DEPTCODE, String V_PER_EDIT) {
        String sql = "MERGE INTO BASE_PERSONTODEPT a USING (SELECT ? as V_PERCODE, ? as V_ORGCODE, ? as V_DEPTCODE FROM dual) b" +
                " ON (a.V_PERCODE = b.V_PERCODE and a.V_ORGCODE = b.V_ORGCODE and a.V_DEPTCODE = b.V_DEPTCODE) WHEN NOT MATCHED THEN INSERT (a.V_PERCODE, a.V_ORGCODE, a.V_DEPTCODE, a.V_PER_EDIT) VALUES (?, ?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_PERCODELIST.get(i));
                ps.setString(2, V_ORGCODE);
                ps.setString(3, V_DEPTCODE);
                ps.setString(4, V_PERCODELIST.get(i));
                ps.setString(5, V_ORGCODE);
                ps.setString(6, V_DEPTCODE);
                ps.setString(7, V_PER_EDIT);
            }

            @Override
            public int getBatchSize() {
                return V_PERCODELIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int deletePersonToDept(String I_ID) {
        String sql = "delete from BASE_PERSONTODEPT where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaPersonToDept(boolean count, String V_PERCODE, String V_ORGCODE, String V_DEPTCODE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from BASE_PERSONTODEPT B where 1 = 1";
        } else {
            sql = "select B.I_ID, B.V_PERCODE, P.V_PERNAME, P.V_LOGINNAME, B.V_DEPTCODE, D.V_DEPTNAME from BASE_PERSONTODEPT B" +
                    " left join BASE_PERSON P on B.V_PERCODE = P.V_PERCODE" +
                    " left join BASE_DEPT D on B.V_DEPTCODE = D.V_DEPTCODE" +
                    " where P.V_STATUS = '1' and D.V_STATUS = '1'";
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and B.V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        if (StringUtils.isNotEmpty(V_ORGCODE)) {
            sql += " and B.V_ORGCODE = :V_ORGCODE";
            paramMap.put("V_ORGCODE", V_ORGCODE);
        }
        if (StringUtils.isNotEmpty(V_DEPTCODE)) {
            sql += " and B.V_DEPTCODE = :V_DEPTCODE";
            paramMap.put("V_DEPTCODE", V_DEPTCODE);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
