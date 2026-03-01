package com.example.todolist.core.util;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static long normalizeDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static int daysBetween(long start, long end) {
        long diff = normalizeDate(end) - normalizeDate(start);
        return (int) TimeUnit.MILLISECONDS.toDays(diff);
    }
}
