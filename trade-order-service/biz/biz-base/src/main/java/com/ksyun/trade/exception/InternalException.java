package com.ksyun.trade.exception;


import com.ksyun.trade.constant.ErrorCodeEnum;

/**
 * 非对外暴露异常:
 * http请求由GlobalExceptionHandler统一处理为系统繁忙.
 *
 * @author ksc
 */
public class InternalException extends RuntimeException {

    private final ErrorCodeEnum errorCodeEnum;

    private String message;

    public InternalException(String message) {
        super();
        this.errorCodeEnum = ErrorCodeEnum.ERROR_DEFAULT;
        this.message = message;
    }

    public InternalException(ErrorCodeEnum errorCodeEnum) {
        super();
        this.errorCodeEnum = errorCodeEnum;
    }

    public InternalException(String message, ErrorCodeEnum errorCodeEnum) {
        super(message);
        this.errorCodeEnum = errorCodeEnum;
        this.message = message;
    }

    public InternalException(String message, Throwable cause) {
        super(cause);
        this.errorCodeEnum = ErrorCodeEnum.ERROR_DEFAULT;
        this.message = message;
    }

    public InternalException(String message, Throwable cause, ErrorCodeEnum errorCodeEnum) {
        super(cause);
        this.errorCodeEnum = errorCodeEnum;
        this.message = message;
    }

    public InternalException(Throwable cause) {
        super(cause);
        message = cause.getMessage();
        this.errorCodeEnum = ErrorCodeEnum.ERROR_DEFAULT;
    }

    public InternalException(Throwable cause, ErrorCodeEnum errorCodeEnum) {
        super(cause);
        this.errorCodeEnum = errorCodeEnum;
    }

    public Integer getErrorCode() {
        return errorCodeEnum.getErrorCode();
    }

    @Override
    public String getMessage() {
        return String.format(errorCodeEnum.getErrorMsg(), message);
    }


}
