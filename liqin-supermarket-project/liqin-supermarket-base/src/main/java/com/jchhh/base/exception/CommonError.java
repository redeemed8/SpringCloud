package com.jchhh.base.exception;

public enum CommonError {

    REQUEST_NULL("请求参数为空!"),
    QUERY_NULL("查询结果为空!"),
    PARAMS_ERROR("非法参数!"),
    UNKNOWN_ERROR("执行过程异常, 请重试!");

    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    CommonError(String errMessage) {
        this.errMessage = errMessage;
    }
}
