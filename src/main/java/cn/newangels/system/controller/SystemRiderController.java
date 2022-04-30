package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.service.SystemRiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 文件
 *
 * @author: MengLuLu
 * @date: 2022/2/21 10:10
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class SystemRiderController {
    private final SystemRiderService systemRiderService;

    /**
     * 附件上传
     */
    @PostMapping("insertSystemRider")
    @Log(title = "系统附件管理", operateType = "附件上传")
    public Map<String, Object> insertSystemRider(String V_TOPIC, String V_DESC, String V_FILETYPE, MultipartFile[] multipartFiles, String V_PERCODE) throws IOException {
        if (systemRiderService.insertSystemRider(V_TOPIC, V_DESC, V_FILETYPE, multipartFiles, V_PERCODE) == 0) {
            return BaseUtils.failed("新增文件失败");
        }
        return BaseUtils.success();
    }
}
