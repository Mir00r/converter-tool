package com.sde.converter.utils;

import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static Logger log = LoggerFactory.getLogger(DateUtil.class);
    private static final String YEAR_MONTH_DAY = "yyyyMMdd";
    private static final String HOUR_MINUTE_SECOND = "HHmmss";
    private static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyyMMddHHmmss";

    public DateUtil() {
    }

    public static String retrieveDateNow() {
        return retrieveDateNowWithFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
    }

    public static String retrieveDateNowWithFormat(String var0) {
        return convertDateWithFormat(new Date(), var0);
    }

    public static String convertDateWithFormat(String var0, String var1, String var2) {
        return convertDateWithFormat(convertDateWithFormat(var0, var1), var2);
    }

    public static String convertDateWithFormat(Date var0, String var1) {
        if (var0 == null) {
            return null;
        } else {
            SimpleDateFormat var2 = new SimpleDateFormat(var1);
            return var2.format(var0);
        }
    }

    public static Date convertDateWithFormat(String var0, String var1) {
        SimpleDateFormat var2 = new SimpleDateFormat(var1);

        try {
            return var2.parse(var0);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            return null;
        }
    }

    public static boolean isDate(String var0, String var1) {
        SimpleDateFormat var2 = new SimpleDateFormat(var1);

        try {
            var2.parse(var0);
            return true;
        } catch (Exception var4) {
            return false;
        }
    }

    public static boolean isDate(Date var0, String var1) {
        SimpleDateFormat var2 = new SimpleDateFormat(var1);

        try {
            var2.format(var0);
            return true;
        } catch (Exception var4) {
            return false;
        }
    }

    public static boolean isToday(Date var0) {
        return DateTimeComparator.getDateOnlyInstance().compare(var0, new Date()) == 0;
    }

    public static boolean isSameDay(Date var0, Date var1) {
        return DateTimeComparator.getDateOnlyInstance().compare(var0, var1) == 0;
    }

    public static boolean greaterThanToday(Date var0) {
        return DateTimeComparator.getDateOnlyInstance().compare(var0, new Date()) > 0;
    }

    public static boolean lesserThanToday(Date var0) {
        return DateTimeComparator.getDateOnlyInstance().compare(var0, new Date()) < 0;
    }

    public static int compareDateOnly(Date var0, Date var1) {
        return DateTimeComparator.getDateOnlyInstance().compare(var0, var1);
    }

    public static int compareTimeOnly(Date var0, Date var1) {
        return DateTimeComparator.getTimeOnlyInstance().compare(var0, var1);
    }

    public static int compareDateAndTime(Date var0, Date var1) {
        return DateTimeComparator.getInstance().compare(var0, var1);
    }

    public static Date plusOrMinusDateTime(Date var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
        Calendar var9 = Calendar.getInstance();
        var9.setTime(var0);
        if (var1 != 0) {
            var9.add(1, var1);
        }

        if (var2 != 0) {
            var9.add(2, var2);
        }

        if (var3 != 0) {
            var9.add(3, var3);
        }

        if (var4 != 0) {
            var9.add(6, var4);
        }

        if (var5 != 0) {
            var9.add(11, var5);
        }

        if (var6 != 0) {
            var9.add(12, var6);
        }

        if (var7 != 0) {
            var9.add(13, var7);
        }

        if (var8 != 0) {
            var9.add(14, var8);
        }

        return var9.getTime();
    }

    public static Date plusDays(Date var0, int var1) {
        return plusOrMinusDateTime(var0, 0, 0, 0, var1, 0, 0, 0, 0);
    }

    public static Date minusDays(Date var0, int var1) {
        return plusOrMinusDateTime(var0, 0, 0, 0, -var1, 0, 0, 0, 0);
    }

    public static Date plusHours(Date var0, int var1) {
        return plusOrMinusDateTime(var0, 0, 0, 0, 0, var1, 0, 0, 0);
    }

    public static Date minusHours(Date var0, int var1) {
        return plusOrMinusDateTime(var0, 0, 0, 0, 0, -var1, 0, 0, 0);
    }
}
