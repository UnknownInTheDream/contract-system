package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.RoleToMenuService;
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
 * @author: JinHongKe
 * @date: 2022/2/10
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class RoleToMenuServiceImpl implements RoleToMenuService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectRoleToMenu(String V_ORLECODE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaRoleToMenu(false, V_ORLECODE);
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
    public int countRoleToMenu(String V_ORLECODE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaRoleToMenu(true, V_ORLECODE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertRoleToMenuBatch(String V_ORLECODE, List<String> V_MENUCODELIST, String V_PER_EDIT) {
        String sql = "MERGE INTO BASE_ROLETOMENU a USING (SELECT ? as V_ORLECODE, ? as V_MENUCODE FROM dual) b" +
                " ON (a.V_ORLECODE = b.V_ORLECODE and a.V_MENUCODE = b.V_MENUCODE ) WHEN NOT MATCHED THEN INSERT (a.V_ORLECODE, a.V_MENUCODE, a.V_PER_EDIT) VALUES (?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_ORLECODE);
                ps.setString(2, V_MENUCODELIST.get(i));
                ps.setString(3, V_ORLECODE);
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
    public int deleteRoleToMenu(String I_ID) {
        String sql = "delete from BASE_ROLETOMENU where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaRoleToMenu(boolean count, String V_ORLECODE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select COUNT(*) from BASE_ROLETOMENU BR WHERE 1 = 1";
        } else {
            sql = "select BR.I_ID, BR.V_MENUCODE, BM.V_NAME, BM.V_ADDRESS, BM.V_ADDRESS_ICO, BM.V_SYSTYPE from BASE_ROLETOMENU BR LEFT JOIN BASE_MENU BM ON BR.V_MENUCODE = BM.I_ID where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_ORLECODE)) {
            sql += " and BR.V_ORLECODE = :V_ORLECODE ";
            paramMap.put("V_ORLECODE", V_ORLECODE);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
