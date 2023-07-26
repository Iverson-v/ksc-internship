package com.ksyun.common.util.time;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

/**
 * Date的parse()与format(), 采用Apache Common Lang中线程安全, 性能更佳的FastDateFormat
 * 
 * 注意Common Lang版本，3.5版才使用StringBuilder，3.4及以前使用StringBuffer.
 * 
 * 1. 常用格式的FastDateFormat定义
 * 2. 日期格式不固定时的String<->Date 转换函数.
 * 3. 打印时间间隔，如"01:10:10"，以及用户友好的版本，比如"刚刚"，"10分钟前"
 * @see FastDateFormat#parse(String)
 * @see FastDateFormat#format(java.util.Date)
 * @see FastDateFormat#format(long)
 */
public final class DateFormatUtil {

	/**
	 * 打印用户友好的，与当前时间相比的时间差，如刚刚，5分钟前，今天XXX，昨天XXX
	 * 
	 * from AndroidUtilCode
	 */
	public static String formatFriendlyTimeSpanByNow(long timeStampMillis) {
		long now = ClockUtil.currentTimeMillis();
		long span = now - timeStampMillis;
		if (span < 0) {
			// 'c' 日期和时间，被格式化为 "%ta %tb %td %tT %tZ %tY"，例如 "Sun Jul 20 16:17:00 EDT 1969"。
			return String.format("%tc", timeStampMillis);
		}
		if (span < DateTimeUtils.MILLIS_PER_SECOND) {
			return "刚刚";
		} else if (span < DateTimeUtils.MILLIS_PER_MINUTE) {
			return String.format("%d秒前", span / DateTimeUtils.MILLIS_PER_SECOND);
		} else if (span < DateTimeUtils.MILLIS_PER_HOUR) {
			return String.format("%d分钟前", span / DateTimeUtils.MILLIS_PER_MINUTE);
		}
		// 获取当天00:00
		long wee = DateTimeUtils.beginOfDate(new Date(now)).getTime();
		if (timeStampMillis >= wee) {
			// 'R' 24 小时制的时间，被格式化为 "%tH:%tM"
			return String.format("今天%tR", timeStampMillis);
		} else if (timeStampMillis >= wee - DateTimeUtils.MILLIS_PER_DAY) {
			return String.format("昨天%tR", timeStampMillis);
		} else {
			// 'F' ISO 8601 格式的完整日期，被格式化为 "%tY-%tm-%td"。
			return String.format("%tF", timeStampMillis);
		}
	}

}
