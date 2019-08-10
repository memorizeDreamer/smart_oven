package com.project.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getCurrentDayString(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static String getCurrentSecString(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * 计算相差固定小时的前面的时间
     * @return Date
     */
    public static Long beforeHourDate(Date date, int count) {
        Calendar to = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        to.setTime(date);
        to.set(Calendar.HOUR_OF_DAY, to.get(Calendar.HOUR_OF_DAY) - count);
        return to.getTimeInMillis();
    }
}
