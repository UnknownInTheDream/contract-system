package cn.newangels.system.feign.factory;

import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.exception.UnExpectedResultException;
import cn.newangels.system.feign.MinioService;
import feign.Response;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: TangLiang
 * @date: 2022/2/25 11:56
 * @since: 1.0
 */
@Component
public class MinioFallbackService implements FallbackFactory<MinioService> {

    @Override
    public MinioService create(Throwable throwable) {
        return new MinioService() {
            @Override
            public Map<String, Object> putObject(String objectName, MultipartFile multipartFile, String V_PERCODE) {
                return BaseUtils.failed("服务调用失败");
            }

            @Override
            public Map<String, Object> putObjectBytes(String objectName, byte[] bytes, String contentType, String V_PERCODE) {
                return BaseUtils.failed("服务调用失败");
            }

            @Override
            public Response downloadFile(String objectName, String fileName, String V_PERCODE) {
                throw new UnExpectedResultException("服务调用失败");
            }

            @Override
            public Map<String, Object> removeObject(String objectName, String V_PERCODE) {
                return BaseUtils.success();
            }

            @Override
            public Map<String, Object> removeObjects(List<String> objectNames, String V_PERCODE) {
                return BaseUtils.success();
            }

            @Override
            public Response downloadRiderList(List<String> objectNames, String zipname, String V_PERCODE) throws IOException {
                throw new UnExpectedResultException("服务调用失败");
            }
        };
    }
}
