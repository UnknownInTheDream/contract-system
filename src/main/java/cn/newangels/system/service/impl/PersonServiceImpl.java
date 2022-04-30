package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseSqlCriteria;
import cn.newangels.system.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TangLiang
 * @date 2022/01/25 11:16
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadPerson(String I_ID) {
        String sql = "select I_ID, V_PERCODE, V_PERNAME, V_LOGINNAME, V_STATUS, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, I_ORDER, D_DATE_CREATE, D_DATE_EDIT, V_PER_EDIT, D_DATE_LOGIN from BASE_PERSON where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public Map<String, Object> loadPersonByCode(String V_PERCODE) {
        String sql = "select I_ID, V_PERCODE, V_PERNAME, V_LOGINNAME, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, D_DATE_LOGIN from BASE_PERSON where V_PERCODE = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, V_PERCODE);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectPerson(String V_PERCODE, String V_PERNAME, String V_STATUS, Integer current, Integer pageSize) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerson(false, V_PERCODE, V_PERNAME, V_STATUS);
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
    public List<Map<String, Object>> selectPersonByLoginName(String V_LOGINNAME) {
        String sql = "select I_ID, V_PASSWORD, V_STATUS, V_PERCODE from BASE_PERSON where V_LOGINNAME = ?";
        return systemJdbcTemplate.queryForList(sql, V_LOGINNAME);
    }

    @Override
    public int countPerson(String V_PERCODE, String V_PERNAME, String V_STATUS) {
        BaseSqlCriteria baseSqlCriteria = buildSqlCriteriaPerson(true, V_PERCODE, V_PERNAME, V_STATUS);// 根据查询条件组装总数统计SQL语句
        String sql = baseSqlCriteria.getSql();
        Map<String, Object> paramMap = baseSqlCriteria.getParamMap();
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    @Override
    public int insertPerson(String I_ID, String V_PERCODE, String V_PERNAME, String V_LOGINNAME, String V_TEL, String V_LXDH_CLF, String V_SFZH, String V_SAPPER, String V_YGCODE, String V_TOAM, String V_AM, String V_ZJ, String V_ZW, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "insert into BASE_PERSON (I_ID, V_PERCODE, V_PERNAME, V_LOGINNAME, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, I_ORDER, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_PERCODE, V_PERNAME, V_LOGINNAME, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, I_ORDER, V_PER_EDIT);
    }

    @Override
    public int updatePerson(String I_ID, String V_PERCODE, String V_PERNAME, String V_LOGINNAME, String V_TEL, String V_LXDH_CLF, String V_SFZH, String V_SAPPER, String V_YGCODE, String V_TOAM, String V_AM, String V_ZJ, String V_ZW, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "update BASE_PERSON set V_PERCODE = ?, V_PERNAME = ?, V_LOGINNAME = ?, V_TEL = ?, V_LXDH_CLF = ?, V_SFZH = ?, V_SAPPER = ?, V_YGCODE = ?, V_TOAM = ?, V_AM = ?, V_ZJ = ?, V_ZW = ?, I_ORDER = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_PERCODE, V_PERNAME, V_LOGINNAME, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, I_ORDER, V_PER_EDIT, I_ID);
    }

    @Override
    public int updatePersonStatus(String I_ID, String V_STATUS, String V_PER_EDIT) {
        String sql = "update BASE_PERSON set V_STATUS = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_STATUS, V_PER_EDIT, I_ID);
    }

    @Override
    public int initPersonPassWord(String I_ID, String V_PASSWORD, String V_PER_EDIT) {
        String sql = "update BASE_PERSON set V_PASSWORD = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_PASSWORD, V_PER_EDIT, I_ID);
    }

    @Override
    public int updatePersonPassWord(String V_OLDPASSWORD, String V_NEWPASSWORD, String V_PERCODE) {
        String sql = "update BASE_PERSON set V_PASSWORD = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where V_PASSWORD = ? and V_PERCODE = ?";
        return systemJdbcTemplate.update(sql, V_NEWPASSWORD, V_PERCODE, V_OLDPASSWORD, V_PERCODE);
    }

    @Override
    @Async
    public void updatePersonLastLogin(String I_ID) {
        String sql = "update BASE_PERSON set D_DATE_LOGIN = sysdate where I_ID = ?";
        systemJdbcTemplate.update(sql, I_ID);
    }

    @Override
    public int deletePerson(String I_ID) {
        String sql = "delete from BASE_PERSON where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

    private BaseSqlCriteria buildSqlCriteriaPerson(boolean count, String V_PERCODE, String V_PERNAME, String V_STATUS) {
        String sql;
        Map<String, Object> paramMap = new HashMap<>(4);
        if (count) {
            sql = "select count(*) from BASE_PERSON where 1 = 1";
        } else {
            sql = "select I_ID, V_PERCODE, V_PERNAME, V_LOGINNAME, V_STATUS, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, I_ORDER, D_DATE_LOGIN from BASE_PERSON where 1 = 1";
        }
        if (StringUtils.isNotEmpty(V_STATUS)) {
            sql += " and V_STATUS = :V_STATUS";
            paramMap.put("V_STATUS", V_STATUS);
        }
        if (StringUtils.isNotEmpty(V_PERNAME)) {
            sql += " and V_PERNAME like '%' || :V_PERNAME || '%'";
            paramMap.put("V_PERNAME", V_PERNAME);
        }
        if (StringUtils.isNotEmpty(V_PERCODE)) {
            sql += " and V_PERCODE = :V_PERCODE";
            paramMap.put("V_PERCODE", V_PERCODE);
        }
        if (!count) {
            sql += " order by I_ORDER";
        }
        return new BaseSqlCriteria(sql, paramMap);
    }

    @Override
    public List<Map<String, Object>> selectPersonByPerCodeList(List<String> PERCODE_LIST) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String sql = "select * from BASEV_EMP where V_PERCODE in (:PERCODE_LIST)";
        paramMap.put("PERCODE_LIST", PERCODE_LIST);
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }
}
