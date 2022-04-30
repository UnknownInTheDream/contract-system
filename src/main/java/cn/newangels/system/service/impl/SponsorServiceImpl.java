package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.SponsorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class SponsorServiceImpl implements SponsorService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadSponsor(String I_ID) {
        String sql = "select * from CON_SPONSOR where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectSponsor(String V_SPONSORCODE, String V_SPONSORNAME, String V_STATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaSponsor(false, V_SPONSORCODE, V_SPONSORNAME, V_STATUS);
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
    public int countSponsor(String V_SPONSORCODE, String V_SPONSORNAME, String V_STATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaSponsor(true, V_SPONSORCODE, V_SPONSORNAME, V_STATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertSponsor(String I_ID, String V_SPONSORCODE, String V_SPONSORNAME, String V_SIMPLENAME, String V_OFFICER, String V_PHONE, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "insert into CON_SPONSOR (I_ID, V_SPONSORCODE, V_SPONSORNAME, V_SIMPLENAME, V_OFFICER, V_PHONE, I_ORDER, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_SPONSORCODE, V_SPONSORNAME, V_SIMPLENAME, V_OFFICER, V_PHONE, I_ORDER, V_PER_EDIT);
    }

    @Override
    public int updateSponsor(String I_ID, String V_SPONSORCODE, String V_SPONSORNAME, String V_SIMPLENAME, String V_OFFICER, String V_PHONE, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "update CON_SPONSOR set V_SPONSORCODE = ?, V_SPONSORNAME = ?, V_SIMPLENAME = ?, V_OFFICER = ?, V_PHONE = ?, I_ORDER = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_SPONSORCODE, V_SPONSORNAME, V_SIMPLENAME, V_OFFICER, V_PHONE, I_ORDER, V_PER_EDIT, I_ID);
    }

    @Override
    public int updateSponsorStatus(String I_ID, String V_STATUS, String V_PER_EDIT) {
        String sql = "update CON_SPONSOR set V_STATUS = ? ,D_DATE_EDIT = sysdate, V_PER_EDIT =? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PER_EDIT, I_ID);
    }

    @Override
    public int deleteSponsor(String I_ID) {
        String sql = "delete CON_SPONSOR where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaSponsor(boolean count, String V_SPONSORCODE, String V_SPONSORNAME, String V_STATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from CON_SPONSOR where 1 = 1";
        } else {
            sql = "select * from CON_SPONSOR where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_STATUS)) {
            sql += " and V_STATUS = :V_STATUS";
            paramMap.put("V_STATUS", V_STATUS);
        }
        if (StringUtils.isNotEmpty(V_SPONSORNAME)) {
            sql += " and V_SPONSORNAME like '%' || :V_SPONSORNAME || '%'";
            paramMap.put("V_SPONSORNAME", V_SPONSORNAME);
        }
        if (StringUtils.isNotEmpty(V_SPONSORCODE)) {
            sql += " and V_SPONSORCODE = :V_SPONSORCODE";
            paramMap.put("V_SPONSORCODE", V_SPONSORCODE);
        }
        if (!count) {
            sql += " order by I_ORDER ASC";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
