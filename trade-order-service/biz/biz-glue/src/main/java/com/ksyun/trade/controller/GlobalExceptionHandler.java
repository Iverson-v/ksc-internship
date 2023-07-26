package com.ksyun.trade.controller;

import com.google.common.base.Strings;
import com.ksyun.common.util.reflect.Reflections;
import com.ksyun.trade.constant.Constant;
import com.ksyun.trade.rest.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局的异常处理.
 *
 * @author ksc
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RestResult handeException(final Exception ex) {
        return RestResult.failure().code(Constant.DEFAULT_ERROR_CODE).msg(ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public RestResult handleBindException(final BindException ex) {
        log.error("参数绑定失败", ex);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
        for (FieldError fieldError : fieldErrors) {
            String error = fieldError.getDefaultMessage();
            errors.add(error);
        }

        for (ObjectError objectError : globalErrors) {
            String error = objectError.getDefaultMessage();
            errors.add(error);
        }
        return RestResult.failure().code(Constant.DEFAULT_ERROR_CODE).msg(StringUtils.join(errors, "|"));
    }


}
