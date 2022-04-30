package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PersonToPostService;
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
 * @date 2022/02/07 14:39
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PersonToPostServiceImpl implements PersonToPostService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectPersonToPost(String V_PERCODE, String V_POSTCODE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPersonToPost(false, V_PERCODE, V_POSTCODE);
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
    public int countPersonToPost(String V_PERCODE, String V_POSTCODE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPersonToPost(true, V_PERCODE, V_POSTCODE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPersonToPostBatch(String V_PERCODE, List<String> V_POSTCODELIST, String V_PER_EDIT) {
        //人员岗位为1对1关系，先删再增
        String delSql = "delete from BASE_PERSONTOPOST where V_PERCODE = ?";
        systemJdbcTemplate.update(delSql, V_PERCODE);
        String sql = "MERGE INTO BASE_PERSONTOPOST a USING (SELECT ? as V_PERCODE, ? as V_POSTCODE FROM dual) b" +
                " ON (a.V_PERCODE = b.V_PERCODE and a.V_POSTCODE = b.V_POSTCODE ) WHEN NOT MATCHED THEN INSERT (a.V_PERCODE, a.V_POSTCODE, a.V_PER_EDIT) VALUES (?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_PERCODE);
                ps.setString(2, V_POSTCODELIST.get(i));
                ps.setString(3, V_PERCODE);
                ps.setString(4, V_POSTCODELIST.get(i));
                ps.setString(5, V_PER_EDIT);
            }

            @Override
            public int getBatchSize() {
                return V_POSTCODELIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int deletePersonToPost(String I_ID) {
        String sql = "delete from BASE_PERSONTOPOST where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaPersonToPost(boolean count, String V_PERCODE, String V_POSTCODE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from BASE_PERSONTOPOST B where 1 = 1";
        } else {
            sql = "select B.I_ID, B.V_POSTCODE, P.V_POSTNAME from BASE_PERSONTOPOST B left join BASE_POST P on B.V_POSTCODE = P.V_POSTCODE where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and B.V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        if (StringUtils.isNotEmpty(V_POSTCODE)) {
            sql += " and B.V_POSTCODE = :V_POSTCODE";
            paramMap.put("V_POSTCODE", V_POSTCODE);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
