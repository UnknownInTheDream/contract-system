package cn.newangels.system.service.impl;

import cn.newangels.system.service.MenuService;
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
 * @date 2022/01/26 09:16
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final JdbcTemplate systemJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> loadMenu(String I_ID) {
        String sql = "select * from BASE_MENU where I_ID = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, I_ID);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public List<Map<String, Object>> selectMenu(String I_PID, String V_CODE, String V_NAME, String V_SYSTYPE) {
        String sql = "select * from BASE_MENU where 1 = 1";
        Map<String, Object> paramMap = new HashMap<>(8);
        if (StringUtils.isNotEmpty(V_NAME)) {
            sql += " and V_NAME like '%' || :V_NAME || '%'";
            paramMap.put("V_NAME", V_NAME);
        }
        if (StringUtils.isNotEmpty(V_SYSTYPE)) {
            sql += " and V_SYSTYPE = :V_SYSTYPE";
            paramMap.put("V_SYSTYPE", V_SYSTYPE);
        }
        if (StringUtils.isNotEmpty(I_PID)) {
            sql += " and I_PID = :I_PID";
            paramMap.put("I_PID", I_PID);
        }
        if (StringUtils.isNotEmpty(V_CODE)) {
            sql += " and I_ID = :V_CODE";
            paramMap.put("V_CODE", V_CODE);
        }
        sql += " order by I_ORDER";
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @Override
    public List<Map<String, Object>> selectPersonMenu(String V_PERCODE, String V_SYSTYPE) {
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("V_PERCODE", V_PERCODE);
        paramMap.put("V_SYSTYPE", V_SYSTYPE);
        String sql = "SELECT I_ID, V_NAME, I_PID, V_ADDRESS, V_ADDRESS_ICO FROM (SELECT M.* FROM BASE_PERSONTOMENU B JOIN BASE_MENU M ON B.V_MENUCODE = M.I_ID" +
                "  WHERE B.V_PERCODE = :V_PERCODE AND M.V_SYSTYPE = :V_SYSTYPE" +
                "  UNION SELECT M.* FROM BASE_ROLETOMENU R JOIN BASE_MENU M ON R.V_MENUCODE = M.I_ID" +
                "  WHERE M.V_SYSTYPE = :V_SYSTYPE AND R.V_ORLECODE IN (SELECT P.V_ORLECODE FROM BASE_PERTOROLE P WHERE P.V_PERCODE = :V_PERCODE)" +
                "  ) X order by X.I_ORDER";
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    @Override
    public int insertMenu(String I_ID, String V_NAME, String I_PID, String V_ADDRESS, String V_ADDRESS_ICO, String V_SYSTYPE, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "insert into BASE_MENU (I_ID, V_NAME, I_PID, V_ADDRESS, V_ADDRESS_ICO, V_SYSTYPE, I_ORDER, V_PER_EDIT) values(?, ?, ?, ?, ?, ?, ?, ?)";
        return systemJdbcTemplate.update(sql, I_ID, V_NAME, I_PID, V_ADDRESS, V_ADDRESS_ICO, V_SYSTYPE, I_ORDER, V_PER_EDIT);
    }

    @Override
    public int updateMenu(String I_ID, String V_NAME, String V_ADDRESS, String V_ADDRESS_ICO, String V_SYSTYPE, Integer I_ORDER, String V_PER_EDIT) {
        String sql = "update BASE_MENU set V_NAME = ?, V_ADDRESS = ?, V_ADDRESS_ICO = ?, V_SYSTYPE = ?, I_ORDER = ?, D_DATE_EDIT = sysdate, V_PER_EDIT = ? where I_ID = ?";
        return systemJdbcTemplate.update(sql, V_NAME, V_ADDRESS, V_ADDRESS_ICO, V_SYSTYPE, I_ORDER, V_PER_EDIT, I_ID);
    }

    @Override
    public int deleteMenu(String I_ID) {
        String sql = "delete from BASE_MENU where I_ID = ?";
        return systemJdbcTemplate.update(sql, I_ID);
    }

}
