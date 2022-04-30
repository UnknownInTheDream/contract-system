package cn.newangels.system.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 登录
 *
 * @author: TangLiang
 * @date: 2022/1/25 10:55
 * @since: 1.0
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param V_LOGINNAME 登录账户
     * @param V_PASSWORD  密码
     * @param request     request
     * @return 成功返回人员信息
     */
    Map<String, Object> login(String V_LOGINNAME, String V_PASSWORD, HttpServletRequest request);
}
