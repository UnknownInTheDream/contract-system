package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.ApprovalOpinionsService;
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
 * @date 2022/03/07 15:02
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ApprovalOpinionsServiceImpl implements ApprovalOpinionsService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadApprovalOpinions(String I_ID) {
        String sql = "select * from CON_APPROVAL_OPINIONS where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectApprovalOpinions(String V_PERCODE, String V_OPINIONS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaApprovalOpinions(false, V_PERCODE, V_OPINIONS);
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
    public int countApprovalOpinions(String V_PERCODE, String V_OPINIONS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaApprovalOpinions(true, V_PERCODE, V_OPINIONS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertApprovalOpinions(String V_PERCODE, String V_OPINIONS) {
        String sql = "insert into CON_APPROVAL_OPINIONS (V_PERCODE, V_OPINIONS) values(?, ?)";
        return systemJdbcTemplate.update(sql, V_PERCODE, V_OPINIONS);
    }

    @Override
    public int updateApprovalOpinions(String I_ID, String V_OPINIONS) {
        String sql = "update CON_APPROVAL_OPINIONS set V_OPINIONS = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_OPINIONS, I_ID);
    }

    @Override
    public int deleteApprovalOpinions(String I_ID) {
        String sql = "delete from CON_APPROVAL_OPINIONS where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaApprovalOpinions(boolean count, String V_PERCODE, String V_OPINIONS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from CON_APPROVAL_OPINIONS where 1 = 1";
        } else {
            sql = "select I_ID, '" + V_PERCODE + "' as V_PERCODE, V_NAME as V_OPINIONS, 1 as V_TYPE from BASE_DICTIONARY where V_DICTIONARYTYPE = '审批用语' union all select I_ID, V_PERCODE, V_OPINIONS, 2 as V_TYPE from CON_APPROVAL_OPINIONS where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        if (StringUtils.isNotEmpty(V_OPINIONS)) {
            sql += " and V_OPINIONS like '%' || :V_OPINIONS || '%'";
            paramMap.put("V_OPINIONS", V_OPINIONS);
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
