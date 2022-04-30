package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.LoginLogService;
import cn.newangels.system.service.LoginService;
import cn.newangels.system.service.PersonService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: TangLiang
 * @date: 2022/1/25 10:55
 * @since: 1.0
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final PersonService personService;
    private final LoginLogService loginLogService;
    private final BaseService baseService;

    @Override
    public Map<String, Object> login(String V_LOGINNAME, String V_PASSWORD, HttpServletRequest request) {
        List<Map<String, Object>> list = personService.selectPersonByLoginName(V_LOGINNAME);
        if (list == null || list.size() == 0) {
            String message = "无当前登录用户, 请修改登录账户";
            Map<String, Object> data = new HashMap<>(4);
            buildLoginLog(V_LOGINNAME, 0, message, request);
            return BaseUtils.failed(message);
        }
        Map<String, Object> person = list.get(0);
        if (!"1".equals(person.get("V_STATUS"))) {
            String message = "用户已被禁用, 请找系统管理员启用";
            buildLoginLog(V_LOGINNAME, 0, message, request);
            return BaseUtils.failed(message);
        }
        if (!V_PASSWORD.equals(person.get("V_PASSWORD"))) {
            String message = "密码错误, 请重新登录";
            buildLoginLog(V_LOGINNAME, 0, message, request);
            return BaseUtils.failed(message);
        }
        personService.updatePersonLastLogin(person.get("I_ID").toString());
        buildLoginLog(V_LOGINNAME, 1, "", request);
        return BaseUtils.loadSuccess(baseService.loadEmpInfoByEmpCode(person.get("V_PERCODE").toString()));
    }

    private void buildLoginLog(String V_LOGINNAME, long V_SUCCESS, String V_ERRMESSAGE, HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        //获取浏览器信息
        Browser browser = userAgent.getBrowser();
        //浏览器版本
        Version version = userAgent.getBrowserVersion();
        //获取操作系统信息
        OperatingSystem os = userAgent.getOperatingSystem();
        loginLogService.insertLoginLog(BaseUtils.getHostName(), V_LOGINNAME, BaseUtils.getIp(request), browser.getName(), version != null ? version.getVersion() : "", os.getName(), V_SUCCESS, V_ERRMESSAGE);
    }
}
