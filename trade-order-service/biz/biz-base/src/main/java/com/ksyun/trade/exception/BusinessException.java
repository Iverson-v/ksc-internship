package com.ksyun.trade.exception;


import com.ksyun.trade.constant.ErrorCodeEnum;

/**
 * 
 * <p>
 * 业务异常基类.
 *
 * @author ksc
 */
public class BusinessException extends RuntimeException {

    private final ErrorCodeEnum errorCodeEnum;

    private String message;

    public BusinessException(String message) {
        super();
        this.errorCodeEnum = ErrorCodeEnum.ERROR_DEFAULT;
        this.message = message;
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        super();
        this.errorCodeEnum = errorCodeEnum;
    }

    public BusinessException(String message, ErrorCodeEnum errorCodeEnum) {
        super(message);
        this.errorCodeEnum = errorCodeEnum;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(cause);
        this.errorCodeEnum = ErrorCodeEnum.ERROR_DEFAULT;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause, ErrorCodeEnum errorCodeEnum) {
        super(cause);
        this.errorCodeEnum = errorCodeEnum;
        this.message = message;
    }

    public BusinessException(Throwable cause) {
        super(cause);
        message = cause.getMessage();
        this.errorCodeEnum = ErrorCodeEnum.ERROR_DEFAULT;
    }

    public BusinessException(Throwable cause, ErrorCodeEnum errorCodeEnum) {
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
