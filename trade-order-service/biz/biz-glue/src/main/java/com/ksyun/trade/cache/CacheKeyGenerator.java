package com.ksyun.trade.cache;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * 
 * 缓存key
 *
 * @author ksc
**/
public class CacheKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		Class clazz = target.getClass();
		if (params.length == 0) {
			return clazz.getName() + "." + method.getName();
		}

		StringBuilder sb = new StringBuilder(".");
		for (Object param : params) {
			if (param != null) {
				sb.append(param);
			}
		}
		return clazz.getName() + "." + method.getName() + sb;
	}
}
