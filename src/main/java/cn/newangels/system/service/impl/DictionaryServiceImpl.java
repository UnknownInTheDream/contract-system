package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.DictionaryService;
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
 * @author TangLiang
 * @date 2022/01/22 16:39
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadDictionary(String I_ID) {
        String sql = "select * from BASE_DICTIONARY where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectDictionary(String V_CODE, String V_NAME, String V_DICTIONARYTYPE, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaDictionary(false, V_CODE, V_NAME, V_DICTIONARYTYPE);
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
    public int countDictionary(String V_CODE, String V_NAME, String V_DICTIONARYTYPE) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaDictionary(true, V_CODE, V_NAME, V_DICTIONARYTYPE);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertDictionary(String I_ID, String V_CODE, String V_NAME, Integer I_ORDER, String V_DICTIONARYTYPE, String V_OTHER, String V_PER_EDIT) {
        String sql = "insert into BASE_DICTIONARY (I_ID, V_CODE, V_NAME, I_ORDER, V_DICTIONARYTYPE, V_OTHER, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_CODE, V_NAME, I_ORDER, V_DICTIONARYTYPE, V_OTHER, V_PER_EDIT);
    }

    @Override
    public int updateDictionary(String I_ID, String V_CODE, String V_NAME, Integer I_ORDER, String V_DICTIONARYTYPE, String V_OTHER, String V_PER_EDIT) {
        String sql = "update BASE_DICTIONARY set V_CODE = ?, V_NAME = ?, I_ORDER = ?, V_DICTIONARYTYPE = ?, V_OTHER = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_CODE, V_NAME, I_ORDER, V_DICTIONARYTYPE, V_OTHER, V_PER_EDIT, I_ID);
    }

    @Override
    public int deleteDictionary(String I_ID) {
        String sql = "delete from BASE_DICTIONARY where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaDictionary(boolean count, String V_CODE, String V_NAME, String V_DICTIONARYTYPE) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from BASE_DICTIONARY where 1 = 1";
        } else {
            sql = "select * from BASE_DICTIONARY where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_CODE)) {
            sql += " and V_CODE = :V_CODE";
            paramMap.put("V_CODE", V_CODE);
        }
        if (StringUtils.isNotEmpty(V_NAME)) {
            sql += " and V_NAME like '%' || :V_NAME || '%'";
            paramMap.put("V_NAME", V_NAME);
        }
        if (StringUtils.isNotEmpty(V_DICTIONARYTYPE)) {
            sql += " and V_DICTIONARYTYPE = :V_DICTIONARYTYPE";
            paramMap.put("V_DICTIONARYTYPE", V_DICTIONARYTYPE);
        }
        if (!count) {
            sql += " order by V_DICTIONARYTYPE, I_ORDER ASC";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
