package com.ksyun.common.util.base;

public final class SystemPropertiesUtils {

    /////////// 简单的合并系统变量(-D)，环境变量 和默认值，以系统变量优先.///////////////

    /**
     * 合并系统变量(-D)，环境变量 和默认值，以系统变量优先
     */
    public static String getString(String propertyName, String envName, String defaultValue) {
        checkEnvName(envName);
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        } else {
            propertyValue = System.getenv(envName);
            return propertyValue != null ? propertyValue : defaultValue;
        }
    }

    /**
     * 检查环境变量名不能有'.'，在linux下不支持
     */
    private static void checkEnvName(String envName) {
        if (envName == null || envName.indexOf('.') != -1) {
            throw new IllegalArgumentException("envName " + envName + " has dot which is not valid");
        }
    }



    private SystemPropertiesUtils() {
    }
}
