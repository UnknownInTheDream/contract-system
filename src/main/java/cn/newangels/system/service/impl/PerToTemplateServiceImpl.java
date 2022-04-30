package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PerToTemplateService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
 * @date 2022/03/07 16:27
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PerToTemplateServiceImpl implements PerToTemplateService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectPerToTemplate(String V_PERCODE, String V_TEMPLATEID, String V_NAME, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerToTemplate(false, V_PERCODE, V_TEMPLATEID, V_NAME);
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
    public int countPerToTemplate(String V_PERCODE, String V_TEMPLATEID, String V_NAME) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerToTemplate(true, V_PERCODE, V_TEMPLATEID, V_NAME);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPerToTemplateBatch(String V_PERCODE, List<String> V_TEMPLATEIDLIST) {
        String sql = "MERGE INTO CON_PERTOTEMPLATE a USING (SELECT ? as V_PERCODE, ? as V_TEMPLATEID FROM dual) b" +
                " ON (a.V_PERCODE = b.V_PERCODE and a.V_TEMPLATEID = b.V_TEMPLATEID ) WHEN NOT MATCHED THEN INSERT (a.V_PERCODE, a.V_TEMPLATEID) VALUES (?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, V_PERCODE);
                ps.setString(2, V_TEMPLATEIDLIST.get(i));
                ps.setString(3, V_PERCODE);
                ps.setString(4, V_TEMPLATEIDLIST.get(i));
            }

            @Override
            public int getBatchSize() {
                return V_TEMPLATEIDLIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int deletePerToTemplateBatch(List<String> I_IDLIST) {
        String sql = "delete from CON_PERTOTEMPLATE where I_ID in (:I_IDLIST)";
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("I_IDLIST", I_IDLIST);
        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    private BaseSqlCriteria buildSqlCriteriaPerToTemplate(boolean count, String V_PERCODE, String V_TEMPLATEID, String V_NAME) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from CON_PERTOTEMPLATE C left join CON_TEMPLATE T on C.V_TEMPLATEID = T.I_ID where T.V_STATUS = '1'";
        } else {
            sql = "select C.I_ID, C.V_TEMPLATEID, T.V_NAME, T.V_TYPEID, T.V_URL from CON_PERTOTEMPLATE C left join CON_TEMPLATE T on C.V_TEMPLATEID = T.I_ID where T.V_STATUS = '1'";
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and C.V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        if (StringUtils.isNotEmpty(V_TEMPLATEID)) {
            sql += " and C.V_TEMPLATEID = :V_TEMPLATEID";
            paramMap.put("V_TEMPLATEID", V_TEMPLATEID);
        }
        if (StringUtils.isNotEmpty(V_NAME)) {
            sql += " and T.V_NAME like '%' || :V_NAME || '%'";
            paramMap.put("V_NAME", V_NAME);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
