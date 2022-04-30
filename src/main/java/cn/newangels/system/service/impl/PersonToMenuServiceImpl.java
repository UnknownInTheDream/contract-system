package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PersonToMenuService;
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
 * @date 2022/02/11 14:54
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PersonToMenuServiceImpl implements PersonToMenuService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectPersonToMenu(String V_PERCODE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPersonToMenu(false, V_PERCODE);
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
    public int countPersonToMenu(String V_PERCODE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPersonToMenu(true, V_PERCODE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPersonToMenuBatch(String V_PERCODE, List<String> V_MENUCODELIST, String V_PER_EDIT) {
        String sql = "MERGE INTO BASE_PERSONTOMENU a USING (SELECT ? as V_PERCODE, ? as V_MENUCODE FROM dual) b" +
                " ON (a.V_PERCODE = b.V_PERCODE and a.V_MENUCODE = b.V_MENUCODE ) WHEN NOT MATCHED THEN INSERT (a.V_PERCODE, a.V_MENUCODE, a.V_PER_EDIT) VALUES (?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_PERCODE);
                ps.setString(2, V_MENUCODELIST.get(i));
                ps.setString(3, V_PERCODE);
                ps.setString(4, V_MENUCODELIST.get(i));
                ps.setString(5, V_PER_EDIT);
            }

            @Override
            public int getBatchSize() {
                return V_MENUCODELIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int deletePersonToMenu(String I_ID) {
        String sql = "delete from BASE_PERSONTOMENU where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaPersonToMenu(boolean count, String V_PERCODE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from BASE_PERSONTOMENU where 1 = 1";
        } else {
            sql = "select p.*, m.V_NAME from BASE_PERSONTOMENU p left join BASE_MENU m on m.i_id = p.v_menucode where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and p.V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
