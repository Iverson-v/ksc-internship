package com.ksyun.trade.constant;


/**
 * @author ksc
 */
public enum ErrorCodeEnum {

    ERROR_BAD_REQUEST(400001, "bad request: %s"),
    ERROR_BAD_REQ_MESSAGE(400003, "%s"),
    ERROR_PARAM(400100, "param error: %s"),

    ERROR_DEFAULT(500000, "%s"),
    ERROR_INTERNAL(500001, "系统繁忙,稍后重试"),

    ERROR_NOT_LOGIN(500005, "未获取到用户信息"),



    ERROR_CAMPAIGN_NOT_EXIST(500300, "您输入的活动ID不存在, 或信息被删除."),

    /*
     * 获取锁失败
     */
    ERROR_LOCK_FAILED(500006, "%s"),


    /**
     * 无权限访问
     */
    ERROR_NO_PERMISSION(500400, "您无权限操作"),


    ERROR_REQUEST_LIMIT(500501, "请求频繁,请稍后重试"),

    ;

    private final Integer errorCode;

    private final String errorMsg;

    ErrorCodeEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
