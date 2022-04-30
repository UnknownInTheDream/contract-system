package cn.newangels.system.service.impl;

import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.PerToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ll
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class BaseServiceImpl implements BaseService {
    private final JdbcTemplate systemJdbcTemplate;
    private final PerToRoleService perToRoleService;

    @Override
    public Map<String, Object> getOperator(String V_PERCODE_FORM) {
        Map<String, Object> operator = new HashMap<>();
        Map<String, Object> posiEmp = loadEmpByEmpCode(V_PERCODE_FORM);
        if (posiEmp != null) {
            operator.put("EMP_ID_", posiEmp.get("V_PERID"));
            operator.put("EMP_CODE_", posiEmp.get("V_PERCODE"));
            operator.put("EMP_NAME_", posiEmp.get("V_PERNAME"));
            operator.put("ORG_ID_", posiEmp.get("V_ORGID"));
            operator.put("ORG_CODE_", posiEmp.get("V_DEPTCODE"));
            operator.put("ORG_NAME_", posiEmp.get("V_DEPTNAME"));
            operator.put("COM_NAME_", posiEmp.get("V_ORGNAME"));
            operator.put("COM_CODE_", posiEmp.get("V_ORGCODE"));
            return operator;
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public Map<String, Object> loadEmpByEmpCode(String V_PERCODE) {
        String sql = "select * from BASEV_EMP where V_PERCODE = ?";
        List<Map<String, Object>> result = systemJdbcTemplate.queryForList(sql, V_PERCODE);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return new HashMap<>(4);
        }
    }

    @Override
    public Map<String, Object> loadEmpInfoByEmpCode(String V_PERCODE) {
        Map<String, Object> map = loadEmpByEmpCode(V_PERCODE);
        List<Map<String, Object>> list = perToRoleService.selectPerToRole(V_PERCODE, null, null, null);
        map.put("V_ROLES", list);
        return map;
    }

    @Override
    public List<Map<String, Object>> selectEmpByDeptRole(String V_DEPTCODE, String V_ROLECODE) {
        String sql = "select * from BASEV_EMPROLETODEPT where V_ORLECODE = ? and V_ORGCODE = (select V_DEPTCODE from BASE_DEPT  where v_depttype='厂矿' start with v_deptcode = ? connect by prior v_deptcode_up = v_deptcode)";
        return systemJdbcTemplate.queryForList(sql, V_ROLECODE, V_DEPTCODE);
    }

    @Override
    public List<Map<String, Object>> selectRoleByEmpCode(String V_PERCODE) {
        String sql = "select * from BASEV_EMPROLETOMAJOR where V_PERCODE = ?";
        return systemJdbcTemplate.queryForList(sql, V_PERCODE);
    }
}
