package com.ksyun.trade.constant;

/**
 * Consts.
 *
 * @author ksc
 */
public final class Constant {

    private Constant() {
    }

    public static final String COMMON_DATE_AND_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String yyyyMMdd = "yyyyMMdd";

    public static final String JSON = "application/json";

    public static final String JSON_UTF_8 = "application/json; charset=UTF-8";

    public static final String INTERNAL_ERROR_DEFAULT_SHOW_MSG = "系统繁忙,稍后重试";

    public static final int NCPU = Runtime.getRuntime().availableProcessors();

    public static final Integer DEFAULT_ERROR_CODE = 500;

    public static final String X_KSC_REQUEST_ID = "X-KSY-REQUEST-ID";

}
