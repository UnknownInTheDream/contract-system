package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.MajorToFlowService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TangLiang
 * @date 2022/03/02 16:59
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MajorToFlowServiceImpl implements MajorToFlowService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaMajorToFlow(false, V_MAJORID, V_CONTYPE, V_FLOWID);
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
    public int countMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaMajorToFlow(true, V_MAJORID, V_CONTYPE, V_FLOWID);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertMajorToFlow(String V_MAJORID, String V_CONTYPE, String V_FLOWID) {
        String sql = "insert into CON_MAJORTOFLOW (V_MAJORID, V_CONTYPE, V_FLOWID) values(?, ?, ?)";
        return systemJdbcTemplate.update(sql, V_MAJORID, V_CONTYPE, V_FLOWID);
    }

    @Override
    public int deleteMajorToFlow(String I_ID) {
        String sql = "delete from CON_MAJORTOFLOW where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    @Override
    public int deleteMajorFlow(String V_MAJORID, String V_CONTYPE) {
        String sql = "delete from CON_MAJORTOFLOW where V_MAJORID = ? and V_CONTYPE = ?";
        return systemJdbcTemplate.update(sql, V_MAJORID, V_CONTYPE);
    }

    private BaseSqlCriteria buildSqlCriteriaMajorToFlow(boolean count, String V_MAJORID, String V_CONTYPE, String V_FLOWID) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(1) from CON_MAJORTOFLOW c where 1 = 1";
        } else {
            sql = "select c.I_ID, c.V_CONTYPE, m.V_NAME, a.NAME_, a.VERSION_, a.KEY_ from CON_MAJORTOFLOW c left join CON_MAJOR m on c.V_MAJORID = m.I_ID left join ACT_RE_PROCDEF a on c.V_FLOWID = a.ID_ where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_MAJORID)) {
            sql += " and c.V_MAJORID = :V_MAJORID";
            paramMap.put("V_MAJORID", V_MAJORID);
        }
        if (StringUtils.isNotEmpty(V_CONTYPE)) {
            sql += " and c.V_CONTYPE = :V_CONTYPE";
            paramMap.put("V_CONTYPE", V_CONTYPE);
        }
        if (StringUtils.isNotEmpty(V_FLOWID)) {
            sql += " and c.V_FLOWID = :V_FLOWID";
            paramMap.put("V_FLOWID", V_FLOWID);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
