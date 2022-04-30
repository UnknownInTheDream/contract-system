package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.TemplateTypeService;
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
 * @date 2022/02/14
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class TemplateTypeServiceImpl implements TemplateTypeService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadTemplateType(String I_ID) {
        String sql = "select * from CON_TEMPLATETYPE where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectTemplateType(String V_CODE, String V_NAME, String V_STATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaTemplateType(false, V_CODE, V_NAME, V_STATUS);
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
    public int countTemplateType(String V_CODE, String V_NAME, String V_STATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaTemplateType(true, V_CODE, V_NAME, V_STATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertTemplateType(String I_ID, String V_CODE, String V_NAME, String I_CONNUMBER, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "insert into CON_TEMPLATETYPE (I_ID, V_CODE, V_NAME, I_CONNUMBER, V_PER_EDIT) values(?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_CODE, V_NAME, I_CONNUMBER, V_PER_EDIT);
    }

    @Override
    public int updateTemplateType(String I_ID, String V_CODE, String V_NAME, String I_CONNUMBER, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "update CON_TEMPLATETYPE set V_CODE = ?, V_NAME = ?, I_CONNUMBER = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_CODE, V_NAME, I_CONNUMBER, V_PER_EDIT, I_ID);
    }

    @Override
    public int updateTemplateTypeStatus(String I_ID, String V_STATUS, String V_PER_EDIT) {
        String sql = "update CON_TEMPLATETYPE set V_STATUS = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PER_EDIT, I_ID);
    }

    @Override
    public int deleteTemplateType(String I_ID) {
        String sql = "delete from CON_TEMPLATETYPE where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaTemplateType(boolean count, String V_CODE, String V_NAME, String V_STATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from CON_TEMPLATETYPE where 1 = 1";
        } else {
            sql = "select * from CON_TEMPLATETYPE where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_NAME)) {
            sql += " and V_NAME like '%' || :V_NAME || '%'";
            paramMap.put("V_NAME", V_NAME);
        }
        if (StringUtils.isNotEmpty(V_STATUS)) {
            sql += " and V_STATUS = :V_STATUS";
            paramMap.put("V_STATUS", V_STATUS);
        }
        if (StringUtils.isNotEmpty(V_CODE)) {
            sql += " and V_CODE = :V_CODE";
            paramMap.put("V_CODE", V_CODE);
        }
        if (!count) {
            sql += " order by I_ORDER";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
