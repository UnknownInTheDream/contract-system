package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.AppointToStampService;
import cn.newangels.system.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ll
 * @date 2022/03/23 15:18
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AppointToStampServiceImpl implements AppointToStampService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ContractService contractService;

    @Override
    public Map<String, Object> loadAppointToStamp(String I_ID) {
        String sql = "select * from CON_APPOINTTOSTAMP where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectAppointToStamp(Date V_BEGIN_DATE, Date V_END_DATE, Integer V_STAMPSTATUS, String V_PER_EDIT, Boolean flag, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaAppointToStamp(false, V_BEGIN_DATE, V_END_DATE, V_STAMPSTATUS, V_PER_EDIT, flag);
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
    public int countAppointToStamp(Date V_BEGIN_DATE, Date V_END_DATE, Integer V_STAMPSTATUS, String V_PER_EDIT, Boolean flag) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaAppointToStamp(true, V_BEGIN_DATE, V_END_DATE, V_STAMPSTATUS, V_PER_EDIT, flag);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertAppointToStampBatch(List<String> V_CONTRACTIDLIST, String V_APPOINTDATE, String V_APPLICANT, String V_PER_EDIT) {
        String sql = "MERGE INTO CON_APPOINTTOSTAMP a USING (SELECT ? as V_CONTRACTID, ? as V_APPOINTDATE, ? as V_APPLICANT FROM dual) b" +
                " ON (a.V_CONTRACTID = b.V_CONTRACTID and a.V_APPOINTDATE = b.V_APPOINTDATE and a.V_APPLICANT = b.V_APPLICANT ) WHEN NOT MATCHED THEN INSERT (a.V_CONTRACTID, a.V_APPOINTDATE, a.V_APPLICANT, a.V_PER_EDIT) VALUES (?, ?, ?, ?)";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Timestamp date = Timestamp.valueOf(V_APPOINTDATE);
                ps.setString(1, V_CONTRACTIDLIST.get(i));
                ps.setTimestamp(2, date);
                ps.setString(3, V_APPLICANT);
                ps.setString(4, V_CONTRACTIDLIST.get(i));
                ps.setTimestamp(5, date);
                ps.setString(6, V_APPLICANT);
                ps.setString(7, V_PER_EDIT);
            }

            @Override
            public int getBatchSize() {
                return V_CONTRACTIDLIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int updateAppointToStamp(String I_ID, String V_APPOINTDATE, Integer V_STAMPSTATUS) {
        String sql = "update CON_APPOINTTOSTAMP set V_APPOINTDATE = ?, V_STAMPSTATUS = ?, D_DATE_EDIT = sysdate where I_ID = ?";
        int count = systemJdbcTemplate.update(sql, V_APPOINTDATE, V_STAMPSTATUS, I_ID);
        return count;
    }

    @Override
    public int updateAppointToStampBatch(List<String> I_IDLIST, String V_APPOINTDATE, Integer V_STAMPSTATUS) {
        String sql = "MERGE INTO CON_APPOINTTOSTAMP a USING (SELECT ? as I_ID, ? as V_APPOINTDATE, ? as V_STAMPSTATUS FROM dual) b" +
                " ON (a.I_ID = b.I_ID) WHEN MATCHED THEN UPDATE SET a.V_APPOINTDATE = ?, a.V_STAMPSTATUS = ?, a.D_DATE_EDIT = sysdate where a.I_ID = ?";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Timestamp date = Timestamp.valueOf(V_APPOINTDATE);
                ps.setString(1, I_IDLIST.get(i));
                ps.setTimestamp(2, date);
                ps.setInt(3, V_STAMPSTATUS);
                ps.setTimestamp(4, date);
                ps.setInt(5, V_STAMPSTATUS);
                ps.setString(6, I_IDLIST.get(i));
            }

            @Override
            public int getBatchSize() {
                return I_IDLIST.size();
            }
        };
        return systemJdbcTemplate.batchUpdate(sql, batch).length;
    }

    @Override
    public int updateStampStatusBatch(List<String> I_IDLIST, Integer V_STAMPSTATUS, List<String> V_CONTRACTIDLIST) {
        String sql = "MERGE INTO CON_APPOINTTOSTAMP a USING (SELECT ? as I_ID, ? as V_STAMPSTATUS FROM dual) b" +
                " ON (a.I_ID = b.I_ID) WHEN MATCHED THEN UPDATE SET a.V_STAMPSTATUS = ?, a.D_DATE_EDIT = sysdate where a.I_ID = ?";
        BatchPreparedStatementSetter batch = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, I_IDLIST.get(i));
                ps.setInt(2, V_STAMPSTATUS);
                ps.setInt(3, V_STAMPSTATUS);
                ps.setString(4, I_IDLIST.get(i));
            }

            @Override
            public int getBatchSize() {
                return I_IDLIST.size();
            }
        };
        int count = systemJdbcTemplate.batchUpdate(sql, batch).length;
        for (String V_CONTRACTID : V_CONTRACTIDLIST) {
            contractService.updateContractStampStatus(V_CONTRACTID, 1);  //合同盖章状态
        }
        return count;
    }

    @Override
    public int deleteAppointToStamp(String I_ID) {
        String sql = "delete from CON_APPOINTTOSTAMP where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaAppointToStamp(boolean count, Date V_BEGIN_DATE, Date V_END_DATE, Integer V_STAMPSTATUS, String V_PER_EDIT, Boolean flag) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(11);
        if (count) {
            sql = "select count(*) from CON_APPOINTTOSTAMP a left join CON_CONTRACT c on a.V_CONTRACTID = c.I_ID where 1 = 1";
        } else {
            sql = "select a.*, c.V_PROJECTCODE, c.V_PROJECTNAME, c.V_CONTRACTCODE, c.V_SPONSORABBR, c.V_CONTRACTORNAME, c.V_MONEY, c.V_INST_STATUS from CON_APPOINTTOSTAMP a left join CON_CONTRACT c on a.V_CONTRACTID = c.I_ID where 1 = 1";
        }
        if (V_BEGIN_DATE != null) {
            sql += " and a.D_DATE_CREATE >= :V_BEGIN_DATE";
            paramMap.put("V_BEGIN_DATE", V_BEGIN_DATE);
        }
        if (V_END_DATE != null) {
            sql += " and a.D_DATE_CREATE <= :V_END_DATE";
            paramMap.put("V_END_DATE", V_END_DATE);
        }
        if (V_STAMPSTATUS != null) {
            sql += " and a.V_STAMPSTATUS = :V_STAMPSTATUS";
            paramMap.put("V_STAMPSTATUS", V_STAMPSTATUS);
        }
        if (flag && StringUtils.isNotEmpty(V_PER_EDIT)) {
            sql += " and a.V_PER_EDIT = :V_PER_EDIT";
            paramMap.put("V_PER_EDIT", V_PER_EDIT);
        }
        if (!count) {
            sql += " order by a.D_DATE_CREATE desc";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }
}
