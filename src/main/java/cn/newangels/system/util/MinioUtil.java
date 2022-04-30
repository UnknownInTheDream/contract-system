package cn.newangels.system.util;

/**
 * @author: TangLiang
 * @date: 2022/2/28 14:21
 * @since: 1.0
 */
public class MinioUtil {
    /**
     * 组装url
     *
     * @param prefix
     * @param id
     * @param fileName
     */
    public static String buildFileUrl(String prefix, String id, String fileName) {
        return prefix + "/" + id + "/" + fileName;
    }
}
