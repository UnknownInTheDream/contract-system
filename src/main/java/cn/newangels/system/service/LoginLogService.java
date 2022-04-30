package cn.newangels.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 登录日志
 *
 * @author: TangLiang
 * @date: 2022/02/21 09:36
 * @since: 1.0
 */
public interface LoginLogService {

    /**
     * 查询登录日志
     *
     * @param V_OPERATEPER 操作人
     * @param V_IP         客户端IP
     * @param V_SUCCESS    1成功/0失败
     * @param current      当前页数
     * @param pageSize     每次显示数量
     */
    List<Map<String, Object>> selectLoginLog(String V_OPERATEPER, String V_IP, Integer V_SUCCESS, Date START_DATE, Date END_DATE, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_OPERATEPER 操作人
     * @param V_IP         客户端IP
     * @param V_SUCCESS    1成功/0失败
     */
    int countLoginLog(String V_OPERATEPER, String V_IP, Integer V_SUCCESS, Date START_DATE, Date END_DATE);

    /**
     * 新增登录日志
     *
     * @param V_HOSTNAME   主机名
     * @param V_OPERATEPER 操作人
     * @param V_IP         客户端IP
     * @param V_BROWSER    浏览器信息
     * @param V_VERSION    浏览器版本
     * @param V_OS         操作系统信息
     * @param V_SUCCESS    1成功/0失败
     * @param V_ERRMESSAGE 错误信息
     */
    void insertLoginLog(String V_HOSTNAME, String V_OPERATEPER, String V_IP, String V_BROWSER, String V_VERSION, String V_OS, long V_SUCCESS, String V_ERRMESSAGE);

}