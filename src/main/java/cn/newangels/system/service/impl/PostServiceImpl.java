package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PostService;
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
 * @date 2022/01/28 11:09
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadPost(String I_ID) {
        String sql = "select * from BASE_POST where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectPost(String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, String V_ISADMIN, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPost(false, V_POSTCODE, V_POSTNAME, V_POSTCODE_UP, V_ISADMIN);
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
    public int countPost(String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, String V_ISADMIN) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPost(true, V_POSTCODE, V_POSTNAME, V_POSTCODE_UP, V_ISADMIN);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPost(String I_ID, String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, Integer I_ORDER, String V_ISADMIN, String V_PER_EDIT) {
        String sql = "insert into BASE_POST (I_ID, V_POSTCODE, V_POSTNAME, V_POSTCODE_UP, I_ORDER, V_ISADMIN, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_POSTCODE, V_POSTNAME, V_POSTCODE_UP, I_ORDER, V_ISADMIN, V_PER_EDIT);
    }

    @Override
    public int updatePost(String I_ID, String V_POSTCODE, String V_POSTNAME, Integer I_ORDER, String V_ISADMIN, String V_PER_EDIT) {
        String sql = "update BASE_POST set V_POSTCODE = ?, V_POSTNAME = ?, I_ORDER = ?, V_ISADMIN = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_POSTCODE, V_POSTNAME, I_ORDER, V_ISADMIN, V_PER_EDIT, I_ID);
    }

    @Override
    public int updatePostStatus(String I_ID, String V_STATUS, String V_PER_EDIT) {
        String sql = "update BASE_POST set V_STATUS = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PER_EDIT, I_ID);
    }

    @Override
    public int deletePost(String I_ID) {
        String sql = "delete from BASE_POST where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaPost(boolean count, String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, String V_ISADMIN) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(8);
        if (count) {
            sql = "select count(*) from BASE_POST where 1 = 1";
        } else {
            sql = "select * from BASE_POST where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_POSTNAME)) {
            sql += " and V_POSTNAME like '%' || :V_POSTNAME || '%'";
            paramMap.put("V_POSTNAME", V_POSTNAME);
        }
        if (StringUtils.isNotEmpty(V_POSTCODE_UP)) {
            sql += " and V_POSTCODE_UP = :V_POSTCODE_UP";
            paramMap.put("V_POSTCODE_UP", V_POSTCODE_UP);
        }
        if (StringUtils.isNotEmpty(V_ISADMIN)) {
            sql += " and V_ISADMIN = :V_ISADMIN";
            paramMap.put("V_ISADMIN", V_ISADMIN);
        }
        if (StringUtils.isNotEmpty(V_POSTCODE)) {
            sql += " and V_POSTCODE = :V_POSTCODE";
            paramMap.put("V_POSTCODE", V_POSTCODE);
        }
        if (!count) {
            sql += " order by I_ORDER";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
