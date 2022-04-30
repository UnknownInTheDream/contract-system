package cn.newangels.system.feign;

import cn.newangels.system.feign.factory.MinioFallbackService;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文件服务调用
 *
 * @author: TangLiang
 * @date: 2022/2/25 10:06
 * @since: 1.0
 */
@FeignClient(value = "contract-rider", fallbackFactory = MinioFallbackService.class)
public interface MinioService {

    /**
     * 上传文件
     *
     * @param objectName    url
     * @param multipartFile 文件
     */
    @PostMapping(value = "putObject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> putObject(@RequestParam String objectName, @RequestPart(value = "multipartFile") MultipartFile multipartFile, @RequestParam String V_PERCODE);

    /**
     * 上传文件(字节)
     *
     * @param objectName  url
     * @param bytes       文件
     * @param contentType 内容类型
     * @param V_PERCODE   人员编码
     */
    @PostMapping(value = "putObjectBytes")
    Map<String, Object> putObjectBytes(@RequestParam String objectName, @RequestBody byte[] bytes, @RequestParam String contentType, @RequestParam String V_PERCODE);

    /**
     * 下载文件
     *
     * @param objectName url
     * @param fileName   下载后文件名
     * @param V_PERCODE  人员编码
     */
    @GetMapping(value = "downloadFile", consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    Response downloadFile(@RequestParam(value = "objectName") String objectName, @RequestParam(value = "fileName") String fileName, @RequestParam(value = "V_PERCODE") String V_PERCODE) throws IOException;

    /**
     * 删除文件
     *
     * @param objectName url
     */
    @PostMapping("removeObject")
    Map<String, Object> removeObject(@RequestParam(value = "objectName") String objectName, @RequestParam(value = "V_PERCODE") String V_PERCODE);

    /**
     * 批量删除文件
     *
     * @param objectNames url集合
     */
    @PostMapping("removeObjects")
    Map<String, Object> removeObjects(@RequestParam(value = "objectNames") List<String> objectNames, @RequestParam(value = "V_PERCODE") String V_PERCODE);

    /**
     * 批量下载文件
     *
     * @param objectNames url集合
     * @param zipName     下载压缩包名
     * @param V_PERCODE   request
     */
    @GetMapping(value = "downloadRiderList", consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    Response downloadRiderList(@RequestParam(value = "objectNames") List<String> objectNames, @RequestParam(value = "zipName") String zipName, @RequestParam String V_PERCODE) throws IOException;

}
