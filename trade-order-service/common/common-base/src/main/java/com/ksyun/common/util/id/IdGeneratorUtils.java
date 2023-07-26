package com.ksyun.common.util.id;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


/**
 * 封装各种生成唯一性ID算法的工具类.
 */
public class IdGeneratorUtils {
	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid2() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String uuid3() {
		return fastUUID().toString().replaceAll("-", "");
	}

	/*
	 * 返回使用ThreadLocalRandom的UUID，比默认的UUID性能更优
	 */
	public static UUID fastUUID() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return new UUID(random.nextLong(), random.nextLong());
	}
}
