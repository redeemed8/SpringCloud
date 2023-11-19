package com.jchhh.base.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RestResponse<T> {

    //  响应
    private Boolean flag;

    //  结果
    private T result;

    //响应提示信息
    private String message;

    public RestResponse() {
    }

    public RestResponse(Boolean flag, T result, String message) {
        this.flag = flag;
        this.result = result;
        this.message = message;
    }

    public static <T> RestResponse<T> ok(T obj, String msg) {
        return new RestResponse<>(true, obj, msg);
    }

    public static <T> RestResponse<T> fail(T obj, String msg) {
        return new RestResponse<>(false, obj, msg);
    }

}
