package com.project.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getCurrentDayString(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static String getCurrentSecString(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}
