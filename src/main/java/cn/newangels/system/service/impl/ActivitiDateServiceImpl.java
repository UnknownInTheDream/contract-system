package cn.newangels.system.service.impl;

import cn.newangels.system.service.ActivitiDateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ActivitiDateServiceImpl implements ActivitiDateService {
    @Override
    public Date nextWorkingDay(int days) {
        //默认为3,爆破需求为2
        days = 3;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (int i = 0; i < days; i++) {
            calendar.add(Calendar.DATE, 1);
            if (!isWorkingDay(calendar)) {
                i--;
            }
        }

        return calendar.getTime();
    }

    private boolean isWorkingDay(Calendar calendar) {
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return false;
        }

        return true;
    }
}
