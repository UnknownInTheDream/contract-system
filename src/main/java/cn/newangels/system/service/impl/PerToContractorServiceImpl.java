package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PerToContractorService;
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
 * @author JinHongKe
 * @date 2022/01/24
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PerToContractorServiceImpl implements PerToContractorService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectPerToContractor(String V_NAME, String V_PERCODE, String CONTRACTOR_ID, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerToContractor(false, V_NAME, V_PERCODE, CONTRACTOR_ID);
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
    public int countPerToContractor(String V_NAME, String V_PERCODE, String CONTRACTOR_ID) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerToContractor(true, V_NAME, V_PERCODE, CONTRACTOR_ID);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPerToContractorBatch(List<String> CONTRACTOR_IDLIST, String V_PERCODE, String V_PER_EDIT) {
        String sql = "MERGE INTO CON_PERTOCONTRACTOR a USING (SELECT ? as CONTRACTOR_ID, ? as V_PERCODE FROM dual) b" +
                " ON (a.CONTRACTOR_ID = b.CONTRACTOR_ID and a.V_PERCODE = b.V_PERCODE ) WHEN NOT MATCHED THEN INSERT (a.CONTRACTOR_ID, a.V_PERCODE, a.V_PER_EDIT) VALUES (?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, CONTRACTOR_IDLIST.get(i));
                ps.setString(2, V_PERCODE);
                ps.setString(3, CONTRACTOR_IDLIST.get(i));
                ps.setString(4, V_PERCODE);
                ps.setString(5, V_PER_EDIT);
            }

            @Override
            public int getBatchSize() {
                return CONTRACTOR_IDLIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int deletePerToContractorBatch(List<String> I_IDLIST) {
        String sql = "delete from CON_PERTOCONTRACTOR where I_ID in (:I_IDLIST)";
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("I_IDLIST", I_IDLIST);
        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    private BaseSqlCriteria buildSqlCriteriaPerToContractor(boolean count, String V_NAME, String V_PERCODE, String CONTRACTOR_ID) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select COUNT(*) from CON_PERTOCONTRACTOR CP left join CON_CONTRACTOR CC on CP.CONTRACTOR_ID = CC.I_ID where CC.V_STATUS = '1'";
        } else {
            sql = "select CP.*, CC.V_NAME, CC.V_ADDRESS, CC.V_LEGAL, CC.V_REPRESENTITIVE, CC.V_PHONE, CC.V_BANK, CC.V_ACCOUNT, CC.V_CREDIT from CON_PERTOCONTRACTOR CP left join CON_CONTRACTOR CC on CP.CONTRACTOR_ID = CC.I_ID where CC.V_STATUS = '1'";
        }
        if (StringUtils.isNotEmpty(V_NAME)) {
            sql += " and CC.V_NAME like '%' || :V_NAME || '%'";
            paramMap.put("V_NAME", V_NAME);
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and CP.V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        if (StringUtils.isNotEmpty(CONTRACTOR_ID)) {
            sql += " and CP.CONTRACTOR_ID = :CONTRACTOR_ID";
            paramMap.put("CONTRACTOR_ID", CONTRACTOR_ID);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
