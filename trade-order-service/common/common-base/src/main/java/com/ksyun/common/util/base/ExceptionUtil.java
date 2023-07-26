package com.ksyun.common.util.base;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.ClassUtils;

/**
 * 关于异常的工具类.
 * 1. 若干常用函数.
 * 2. StackTrace性能优化相关，尽量使用静态异常避免异常生成时获取StackTrace，及打印StackTrace的消耗
 */
public final class ExceptionUtil {

    /**
     * 获取异常的Root Cause.
     * 如无底层Cause, 则返回自身
     *
     * @see Throwables#getRootCause(Throwable)
     */
    public static Throwable getRootCause(Throwable t) {
        return Throwables.getRootCause(t);
    }


    public static String getErrorMessageWithRootAndNestedException(Throwable ex) {
        Throwable rootException = getRootCause(ex);
        Throwable nestedException = ex.getCause();

        if (ex == rootException) {
            StringBuilder sb = new StringBuilder();
            sb.append(ex.getMessage());
            if (nestedException != null) {
                sb.append(" nested exception is ")
                    .append(ClassUtils.getShortClassName(nestedException.getClass())).append(":")
                    .append(nestedException.getMessage());
            }
            return sb.toString();
        }

        if (rootException == nestedException) {
            StringBuilder sb = new StringBuilder();
            sb.append(ex.getMessage());
            if (nestedException != null) {
                sb.append(" nested exception is ")
                    .append(ClassUtils.getShortClassName(nestedException.getClass())).append(":")
                    .append(nestedException.getMessage());
            }
            return sb.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ex.getMessage());
        if (nestedException != null) {
            sb.append(" nested exception is ")
                .append(ClassUtils.getShortClassName(nestedException.getClass())).append(":")
                .append(nestedException.getMessage());
        }
        if (rootException != null) {
            sb.append(" root exception is ")
                .append(ClassUtils.getShortClassName(rootException.getClass())).append(":")
                .append(rootException.getMessage());
        }
        return sb.toString();
    }

    private ExceptionUtil() {
    }
}
