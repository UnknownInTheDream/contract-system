package cn.newangels.system.controller;

import cn.newangels.system.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 登录
 *
 * @author: TangLiang
 * @date: 2022/1/25 10:56
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    /**
     * 登录
     */
    @PostMapping("login")
    public Map<String, Object> login(String username, String password, HttpServletRequest request) {
        return loginService.login(username, password, request);
    }

}
