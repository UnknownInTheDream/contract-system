package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.MajorService;
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
 * @author ll
 * @date 2022/02/11 10:31
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectMajor(String V_DEPTCODE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaMajor(false, V_DEPTCODE);
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
    public int countMajor(String V_DEPTCODE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaMajor(true, V_DEPTCODE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertMajorBatch(List<String> V_NAMELIST, String V_DEPTCODE, String V_PER_EDIT) {
        String sql = "MERGE INTO CON_MAJOR a USING (SELECT ? as V_NAME, ? as V_DEPTCODE FROM dual) b" +
                " ON (a.V_NAME = b.V_NAME and a.V_DEPTCODE = b.V_DEPTCODE ) WHEN NOT MATCHED THEN INSERT (a.V_NAME, a.V_DEPTCODE, a.V_PER_EDIT) VALUES (?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_NAMELIST.get(i));
                ps.setString(2, V_DEPTCODE);
                ps.setString(3, V_NAMELIST.get(i));
                ps.setString(4, V_DEPTCODE);
                ps.setString(5, V_PER_EDIT);
            }

            @Override
            public int getBatchSize() {
                return V_NAMELIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int deleteMajor(String I_ID) {
        String sql = "delete from CON_MAJOR where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaMajor(boolean count, String V_DEPTCODE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from CON_MAJOR where 1 = 1";
        } else {
            sql = "select * from CON_MAJOR where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_DEPTCODE)) {
            sql += " and V_DEPTCODE = :V_DEPTCODE";
            paramMap.put("V_DEPTCODE", V_DEPTCODE);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
