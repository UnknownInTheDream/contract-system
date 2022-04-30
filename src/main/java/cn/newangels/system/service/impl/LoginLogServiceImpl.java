package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TangLiang
 * @date 2022/02/21 09:36
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Map<String, Object>> selectLoginLog(String V_OPERATEPER, String V_IP, Integer V_SUCCESS, Date START_DATE, Date END_DATE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaLoginLog(false, V_OPERATEPER, V_IP, V_SUCCESS, START_DATE, END_DATE);
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
    public int countLoginLog(String V_OPERATEPER, String V_IP, Integer V_SUCCESS, Date START_DATE, Date END_DATE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaLoginLog(true, V_OPERATEPER, V_IP, V_SUCCESS, START_DATE, END_DATE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    @Async
    public void insertLoginLog(String V_HOSTNAME, String V_OPERATEPER, String V_IP, String V_BROWSER, String V_VERSION, String V_OS, long V_SUCCESS, String V_ERRMESSAGE) {
        String sql = "insert into SYS_LOGINLOG (V_HOSTNAME, V_OPERATEPER, V_IP, V_BROWSER, V_VERSION, V_OS, V_CREATETIME, V_SUCCESS, V_ERRMESSAGE) values(?, ?, ?, ?, ?, ?, sysdate, ?, ?)";
        systemJdbcTemplate.update(sql, V_HOSTNAME, V_OPERATEPER, V_IP, V_BROWSER, V_VERSION, V_OS, V_SUCCESS, V_ERRMESSAGE);
    }

    private BaseSqlCriteria buildSqlCriteriaLoginLog(boolean count, String V_OPERATEPER, String V_IP, Integer V_SUCCESS, Date START_DATE, Date END_DATE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from SYS_LOGINLOG where 1 = 1";
        } else {
            sql = "select * from SYS_LOGINLOG where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_OPERATEPER)) {
            sql += " and V_OPERATEPER = :V_OPERATEPER";
            paramMap.put("V_OPERATEPER", V_OPERATEPER);
        }
        if (StringUtils.isNotEmpty(V_IP)) {
            sql += " and V_IP = :V_IP";
            paramMap.put("V_IP", V_IP);
        }
        if (END_DATE != null) {
            sql += " and V_CREATETIME <= :END_DATE";
            paramMap.put("END_DATE", END_DATE);
        }
        if (START_DATE != null) {
            sql += " and V_CREATETIME >= :START_DATE";
            paramMap.put("START_DATE", START_DATE);
        }
        if (V_SUCCESS != null) {
            sql += " and V_SUCCESS = :V_SUCCESS";
            paramMap.put("V_SUCCESS", V_SUCCESS);
        }
        if (!count) {
            sql += " order by V_CREATETIME DESC";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
