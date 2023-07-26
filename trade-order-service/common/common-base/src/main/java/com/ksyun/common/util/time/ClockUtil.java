package com.ksyun.common.util.time;

import java.util.Date;

/**
 * 日期提供者, 使用它而不是直接取得系统时间, 方便测试.
 * 平时使用DEFAULT，测试时替换为DummyClock，可准确控制时间变化而不用Thread.sleep()等待时间流逝.
 */
public final class ClockUtil {

	private static Clock INSTANCE = new DefaultClock();


	/**
	 * 系统当前时间戳
	 */
	public static long currentTimeMillis() {
		return INSTANCE.currentTimeMillis();
	}

	/**
	 * 操作系统启动到现在的纳秒数，与系统时间是完全独立的两个时间体系
	 */
	public static long nanoTime() {
		return INSTANCE.nanoTime();
	}

	public interface Clock {

		/**
		 * 系统当前时间
		 */
		Date currentDate();

		/**
		 * 系统当前时间戳
		 */
		long currentTimeMillis();

		/**
		 * 操作系统启动到现在的纳秒数，与系统时间是完全独立的两个时间体系
		 */
		long nanoTime();
	}

	/**
	 * 默认时间提供者，返回当前的时间，线程安全。
	 */
	public static class DefaultClock implements Clock {

		@Override
		public Date currentDate() {
			return new Date();
		}

		@Override
		public long currentTimeMillis() {
			return System.currentTimeMillis();
		}

		@Override
		public long nanoTime() {
			return System.nanoTime();
		}
	}


}
