package cn.newangels.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统日志
 *
 * @author: TangLiang
 * @date: 2022/1/20 9:32
 * @since: 1.0
 */
public interface LogService {

    /**
     * 加载日志详情信息
     *
     * @param I_ID i_id
     */
    Map<String, Object> loadLog(String I_ID);

    /**
     * 查询日志
     *
     * @param V_SERVICE     服务名
     * @param V_TITLE       模块名
     * @param V_OPERATEPER  操作人
     * @param V_IP          客户端IP
     * @param V_OPERATETYPE 操作类型
     * @param V_SUCCESS     1成功/0失败
     * @param V_URL         请求路径
     * @param V_PARAMS      请求参数
     * @param I_TRACEID     traceid
     * @param V_PROVERSION  项目版本
     * @param START_DATE    开始时间
     * @param END_DATE      结束时间
     * @param current       当前页数
     * @param pageSize      每页多少条
     */
    List<Map<String, Object>> selectLog(String V_SERVICE, String V_TITLE, String V_OPERATEPER, String V_IP, String V_OPERATETYPE, Integer V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_PROVERSION, Date START_DATE, Date END_DATE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_SERVICE     服务名
     * @param V_TITLE       模块名
     * @param V_OPERATEPER  操作人
     * @param V_IP          客户端IP
     * @param V_OPERATETYPE 操作类型
     * @param V_SUCCESS     1成功/0失败
     * @param V_URL         请求路径
     * @param V_PARAMS      请求参数
     * @param I_TRACEID     traceid
     * @param V_PROVERSION  项目版本
     * @param START_DATE    开始时间
     * @param END_DATE      结束时间
     */
    int countLog(String V_SERVICE, String V_TITLE, String V_OPERATEPER, String V_IP, String V_OPERATETYPE, Integer V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_PROVERSION, Date START_DATE, Date END_DATE);

    /**
     * 新增日志
     *
     * @param V_SERVICE     服务名
     * @param V_TITLE       模块名
     * @param V_HOSTNAME    主机名
     * @param V_OPERATEPER  操作人
     * @param V_IP          客户端IP
     * @param V_BROWSER     浏览器信息
     * @param V_VERSION     浏览器版本
     * @param V_OS          操作系统信息
     * @param V_OPERATETYPE 操作类型
     * @param V_SIGNATURE   请求方法名
     * @param V_SUCCESS     1成功/0失败
     * @param V_URL         请求路径
     * @param V_PARAMS      请求参数
     * @param I_TRACEID     traceid
     * @param V_ERRMESSAGE  错误信息
     * @param V_PROVERSION  项目版本
     */
    void insertLog(String V_SERVICE, String V_TITLE, String V_HOSTNAME, String V_OPERATEPER, String V_IP, String V_BROWSER, String V_VERSION, String V_OS, String V_OPERATETYPE, String V_SIGNATURE, long V_SUCCESS, String V_URL, String V_PARAMS, String I_TRACEID, String V_ERRMESSAGE, String V_PROVERSION);

}
