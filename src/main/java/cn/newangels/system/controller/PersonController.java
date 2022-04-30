package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 人员
 *
 * @author: TangLiang
 * @date: 2022/01/25 11:16
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;
    private final BaseService baseService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载人员信息
     */
    @GetMapping("loadPerson")
    @Log(title = "人员管理", operateType = "加载人员信息")
    public Map<String, Object> loadPerson(String I_ID) {
        return BaseUtils.loadSuccess(personService.loadPerson(I_ID));
    }

    /**
     * 根据编码加载人员信息
     */
    @GetMapping("loadEmpInfoByEmpCode")
    @Log(title = "人员管理", operateType = "根据编码加载人员信息")
    public Map<String, Object> loadEmpInfoByEmpCode(String V_PERCODE) {
        return BaseUtils.loadSuccess(baseService.loadEmpInfoByEmpCode(V_PERCODE));
    }

    /**
     * 查询人员
     */
    @GetMapping("selectPerson")
    @Log(title = "人员管理", operateType = "查询人员")
    public Map<String, Object> selectPerson(String V_PERCODE_FORM, String V_PERNAME, String V_STATUS, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = personService.selectPerson(V_PERCODE_FORM, V_PERNAME, V_STATUS, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = personService.countPerson(V_PERCODE_FORM, V_PERNAME, V_STATUS);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 新增人员
     */
    @PostMapping("insertPerson")
    @Log(title = "人员管理", operateType = "新增人员")
    public Map<String, Object> insertPerson(String V_PERCODE_FORM, String V_PERNAME, String V_LOGINNAME, String V_TEL, String V_LXDH_CLF, String V_SFZH, String V_SAPPER, String V_YGCODE, String V_TOAM, String V_AM, String V_ZJ, String V_ZW, Integer I_ORDER, String V_PERCODE) {
        if (personService.insertPerson(String.valueOf(snowflakeIdWorker.nextId()), V_PERCODE_FORM, V_PERNAME, V_LOGINNAME, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("新增人员失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改人员
     */
    @PostMapping("updatePerson")
    @Log(title = "人员管理", operateType = "修改人员")
    public Map<String, Object> updatePerson(String I_ID, String V_PERCODE_FORM, String V_PERNAME, String V_LOGINNAME, String V_TEL, String V_LXDH_CLF, String V_SFZH, String V_SAPPER, String V_YGCODE, String V_TOAM, String V_AM, String V_ZJ, String V_ZW, Integer I_ORDER, String V_PERCODE) {
        if (personService.updatePerson(I_ID, V_PERCODE_FORM, V_PERNAME, V_LOGINNAME, V_TEL, V_LXDH_CLF, V_SFZH, V_SAPPER, V_YGCODE, V_TOAM, V_AM, V_ZJ, V_ZW, I_ORDER, V_PERCODE) == 0) {
            return BaseUtils.failed("修改人员失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改人员状态
     */
    @PostMapping("updatePersonStatus")
    @Log(title = "人员管理", operateType = "修改人员状态")
    public Map<String, Object> updatePersonStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (personService.updatePersonStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改人员状态失败");
        }
        return BaseUtils.success();
    }

    /**
     * 初始化人员密码
     */
    @PostMapping("initPersonPassWord")
    @Log(title = "人员管理", operateType = "初始化人员密码")
    public Map<String, Object> initPersonPassWord(String I_ID, String V_PASSWORD, String V_PERCODE) {
        if (personService.initPersonPassWord(I_ID, V_PASSWORD, V_PERCODE) == 0) {
            return BaseUtils.failed("修改人员密码失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改人员密码
     */
    @PostMapping("updatePersonPassWord")
    @Log(title = "人员管理", operateType = "修改人员密码")
    public Map<String, Object> updatePersonPassWord(String V_OLDPASSWORD, String V_NEWPASSWORD, String V_PERCODE) {
        if (personService.updatePersonPassWord(V_OLDPASSWORD, V_NEWPASSWORD, V_PERCODE) == 0) {
            return BaseUtils.failed("修改人员密码失败，旧密码错误");
        }
        return BaseUtils.success();
    }

    /**
     * 删除人员
     */
    @PostMapping("deletePerson")
    @Log(title = "人员管理", operateType = "删除人员")
    public Map<String, Object> deletePerson(String I_ID) {
        if (personService.deletePerson(I_ID) == 0) {
            return BaseUtils.failed("删除人员失败");
        }
        return BaseUtils.success();
    }

}
