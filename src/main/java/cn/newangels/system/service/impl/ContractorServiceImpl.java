package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.ContractorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class ContractorServiceImpl implements ContractorService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadContractor(String I_ID) {
        String sql = "select * from CON_CONTRACTOR where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectContractor(String V_NAME, String V_STATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContractor(false, V_NAME, V_STATUS);
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
    public int countContractor(String V_NAME, String V_STATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaContractor(true, V_NAME, V_STATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertContractor(String I_ID, String V_NAME, String V_ADDRESS, String V_LEGAL, String V_REPRESENTITIVE, String V_PHONE, String V_BANK, String V_ACCOUNT, String V_NATURE, BigDecimal I_REGEREDCAPITAL, String V_LICENSE, String V_CREDIT, String V_PER_EDIT) {
        String sql = "insert into CON_CONTRACTOR (I_ID, V_NAME, V_ADDRESS, V_LEGAL, V_REPRESENTITIVE, V_PHONE, V_BANK, V_ACCOUNT, V_NATURE, I_REGEREDCAPITAL, V_LICENSE, V_CREDIT, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_NAME, V_ADDRESS, V_LEGAL, V_REPRESENTITIVE, V_PHONE, V_BANK, V_ACCOUNT, V_NATURE, I_REGEREDCAPITAL, V_LICENSE, V_CREDIT, V_PER_EDIT);
    }

    @Override
    public int updateContractor(String I_ID, String V_NAME, String V_ADDRESS, String V_LEGAL, String V_REPRESENTITIVE, String V_PHONE, String V_BANK, String V_ACCOUNT, String V_NATURE, BigDecimal I_REGEREDCAPITAL, String V_LICENSE, String V_CREDIT, String V_PER_EDIT) {
        String sql = "update CON_CONTRACTOR set V_NAME = ?, V_ADDRESS = ?, V_LEGAL = ?, V_REPRESENTITIVE = ?, V_PHONE = ?, V_BANK = ?, V_ACCOUNT = ?, V_NATURE = ?, I_REGEREDCAPITAL = ?, V_LICENSE = ?, V_CREDIT = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_NAME, V_ADDRESS, V_LEGAL, V_REPRESENTITIVE, V_PHONE, V_BANK, V_ACCOUNT, V_NATURE, I_REGEREDCAPITAL, V_LICENSE, V_CREDIT, V_PER_EDIT, I_ID);
    }

    @Override
    public int updateContractorStatus(String I_ID, String V_STATUS, String V_PER_EDIT) {
        String sql = "update CON_CONTRACTOR set V_STATUS = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PER_EDIT, I_ID);
    }

    @Override
    public int deleteContractor(String I_ID) {
        String sql = "delete from CON_CONTRACTOR where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaContractor(boolean count, String V_NAME, String V_STATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from CON_CONTRACTOR where 1 = 1";
        } else {
            sql = "select t.*, I_ID as CONTRACTOR_ID from CON_CONTRACTOR t where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_STATUS)) {
            sql += " and V_STATUS = :V_STATUS";
            paramMap.put("V_STATUS", V_STATUS);
        }
        if (StringUtils.isNotEmpty(V_NAME)) {
            sql += " and V_NAME like '%' || :V_NAME || '%'";
            paramMap.put("V_NAME", V_NAME);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
