package com.ksyun.common.util.time;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }


    /**
     * 每秒毫秒数
     */
    public static final long MILLIS_PER_SECOND = 1000;

    /**
     * 每分毫秒数 60*1000
     */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;

    /**
     * 每小时毫秒数 36*60*1000
     */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    /**
     * 每天毫秒数 24*60*60*1000;
     */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;


    /**
     * 判断日期是否在范围内，包含相等的日期
     */
    public static boolean isBetween(final Date date, final Date start, final Date end) {
        if (date == null || start == null || end == null || start.after(end)) {
            throw new IllegalArgumentException("some date parameters is null or dateBein after dateEnd");
        }
        return !date.before(start) && !date.after(end);
    }


       /**
     * 2016-11-10 07:33:23, 则返回2016-11-10 00:00:00
     */
    public static Date beginOfDate(final Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }

    /**
     * 2017-1-23 07:33:23, 则返回2017-1-23 23:59:59.999
     */
    public static Date endOfDate(final Date date) {
        return new Date(nextDate(date).getTime() - 1);
    }

    /**
     * 2016-11-10 07:33:23, 则返回2016-11-11 00:00:00
     */
    public static Date nextDate(final Date date) {
        return DateUtils.ceiling(date, Calendar.DATE);
    }


    public static Date parse(String timeStr, String formatPattern) {
        try {
            return new SimpleDateFormat(formatPattern).parse(timeStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("could not parse date: " + timeStr
                + " LEGACY_FORMAT = "
                + new SimpleDateFormat(formatPattern).toPattern(), e);
        }
    }

    public static String format(Date date, String formatPattern) {
        if (date == null || StringUtils.isBlank(formatPattern)) {
            throw new IllegalArgumentException("待格式化的时间或时间格式化模式串为空");
        }
        return new SimpleDateFormat(formatPattern).format(date);
    }

}
