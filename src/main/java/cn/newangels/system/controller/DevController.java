package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 开发环境接口
 *
 * @author: TangLiang
 * @date: 2022/1/19 15:05
 * @since: 1.0
 */
@RestController
@Profile(value = {"dev", "test"})
@RequiredArgsConstructor
public class DevController {

    @GetMapping("selectDev")
    @Log
    public Map<String, Object> selectDev(HttpServletRequest request) {
        return BaseUtils.success();
    }
}
