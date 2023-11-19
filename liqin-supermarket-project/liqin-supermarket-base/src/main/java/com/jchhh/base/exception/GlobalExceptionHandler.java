package com.jchhh.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


//@RestControllerAdvice
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常
     *
     * @param e 捕获的自定义异常
     * @return 封装后返回前端的异常
     */
    @ResponseBody
    @ExceptionHandler(LiQinException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(LiQinException e) {
        //  记录异常
        log.error("系统异常:{}", e.getErrMessage(), e);
        //  解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }

    /**
     * 系统原有的异常
     *
     * @param e 捕获的系统异常
     * @return 封装后返回前端的异常
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        //  记录异常
        log.error("系统异常:{}", e.getMessage(), e);
        //  解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }

    /**
     * JSR303 校验的异常
     *
     * @param e 捕获的 JSR303的 MethodArgumentNotValidException异常
     * @return 封装后返回前端的异常
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        //  存储错误信息
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(item -> {
            errors.add(item.getDefaultMessage());
        });
        //  将 list中的错误信息拼接起来
        String errMessage = StringUtils.join(errors, ",");
        //  记录异常
        log.error("系统异常:{}", e.getMessage(), errMessage);
        //  解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }

}
