package cn.newangels.system.service;

import java.util.Date;

/**
 * Map<String, Object> service
 *
 * @author accipiter 2014-04-10
 */
public interface ActivitiDateService {
    /**
     * 获得从今天开始，隔N个工作日的日期
     *
     * @param days 工作日天数
     * @return 日期
     */
    Date nextWorkingDay(int days);
}
