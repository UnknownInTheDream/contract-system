package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.LogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: TangLiang
 * @date: 2022/1/20 9:32
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadLog(String I_ID) {
        String sql = "select * from SYS_LOG where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectLog(String V_SERVICE, String V_TITLE, String V_OPERATEPER, String V_IP, String V_OPERATETYPE, Integer V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_PROVERSION, Date START_DATE, Date END_DATE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaLog(false, V_SERVICE, V_TITLE, V_OPERATEPER, V_IP, V_OPERATETYPE, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_PROVERSION, START_DATE, END_DATE);
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
    public int countLog(String V_SERVICE, String V_TITLE, String V_OPERATEPER, String V_IP, String V_OPERATETYPE, Integer V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_PROVERSION, Date START_DATE, Date END_DATE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaLog(true, V_SERVICE, V_TITLE, V_OPERATEPER, V_IP, V_OPERATETYPE, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_PROVERSION, START_DATE, END_DATE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public void insertLog(String V_SERVICE, String V_TITLE, String V_HOSTNAME, String V_OPERATEPER, String V_IP, String V_BROWSER, String V_VERSION, String V_OS, String V_OPERATETYPE, String V_SIGNATURE, long V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_ERRMESSAGE, String V_PROVERSION) {
        String sql = "insert into SYS_LOG (V_SERVICE, V_TITLE, V_HOSTNAME, V_OPERATEPER, V_IP, V_BROWSER, V_VERSION, V_OS, V_OPERATETYPE, V_SIGNATURE, V_CREATETIME, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_ERRMESSAGE, V_PROVERSION) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, ?, ?, ?, ?, ?, ?)";
        systemJdbcTemplate.update(sql, V_SERVICE, V_TITLE, V_HOSTNAME, V_OPERATEPER, V_IP, V_BROWSER, V_VERSION, V_OS, V_OPERATETYPE, V_SIGNATURE, V_SUCCESS, V_URL, V_PARAMS, I_TRACEID, V_ERRMESSAGE, V_PROVERSION);
    }

    private BaseSqlCriteria buildSqlCriteriaLog(boolean count, String V_SERVICE, String V_TITLE, String V_OPERATEPER, String V_IP, String V_OPERATETYPE, Integer V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_PROVERSION, Date START_DATE, Date END_DATE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(16);
        if (count) {
            sql = "select count(1) from SYS_LOG where 1 = 1";
        } else {
            sql = "select I_ID, V_SERVICE, V_TITLE, V_HOSTNAME, V_OPERATEPER, V_IP, V_OPERATETYPE, V_CREATETIME, V_SUCCESS, I_TRACEID from SYS_LOG where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_PARAMS)) {
            sql += " and INSTR(V_PARAMS,'" + V_PARAMS + "')>0";
            paramMap.put("V_PARAMS", V_PARAMS);
        }
        if (StringUtils.isNotEmpty(V_URL)) {
            sql += " and INSTR(V_URL,'" + V_URL + "')>0";
            paramMap.put("V_URL", V_URL);
        }
        if (StringUtils.isNotEmpty(V_TITLE)) {
            sql += " and V_TITLE like :V_TITLE || '%'";
            paramMap.put("V_TITLE", V_TITLE);
        }
        if (StringUtils.isNotEmpty(V_OPERATEPER)) {
            sql += " and V_OPERATEPER like :V_OPERATEPER || '%'";
            paramMap.put("V_OPERATEPER", V_OPERATEPER);
        }
        if (StringUtils.isNotEmpty(V_OPERATETYPE)) {
            sql += " and V_OPERATETYPE like '%' || :V_OPERATETYPE || '%'";
            paramMap.put("V_OPERATETYPE", V_OPERATETYPE);
        }
        if (END_DATE != null) {
            sql += " and V_CREATETIME <= :END_DATE";
            paramMap.put("END_DATE", END_DATE);
        }
        if (START_DATE != null) {
            sql += " and V_CREATETIME >= :START_DATE";
            paramMap.put("START_DATE", START_DATE);
        }
        if (StringUtils.isNotEmpty(V_PROVERSION)) {
            sql += " and V_PROVERSION = :V_PROVERSION";
            paramMap.put("V_PROVERSION", V_PROVERSION);
        }
        if (V_SUCCESS != null) {
            sql += " and V_SUCCESS = :V_SUCCESS";
            paramMap.put("V_SUCCESS", V_SUCCESS);
        }
        if (StringUtils.isNotEmpty(V_SERVICE)) {
            sql += " and V_SERVICE = :V_SERVICE";
            paramMap.put("V_SERVICE", V_SERVICE);
        }
        if (StringUtils.isNotEmpty(V_IP)) {
            sql += " and V_IP = :V_IP";
            paramMap.put("V_IP", V_IP);
        }
        if (StringUtils.isNotEmpty(I_TRACEID)) {
            sql += " and I_TRACEID = :I_TRACEID";
            paramMap.put("I_TRACEID", I_TRACEID);
        }
        if (!count) {
            sql += " order by V_CREATETIME DESC";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
