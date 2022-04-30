package cn.newangels.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件
 *
 * @author: MengLuLu
 * @date: 2022/2/21 10:14
 * @since: 1.0
 */
public interface SystemRiderService {
    /**
     * 附件上传
     *
     * @param V_TOPIC    文件主题
     * @param V_DESC     文件描述
     * @param V_FILETYPE 文件类型
     * @param V_PERCODE 最后修改人
     */
    int insertSystemRider(String V_TOPIC, String V_DESC, String V_FILETYPE, MultipartFile[] multipartFiles, String V_PERCODE) throws IOException;

}
