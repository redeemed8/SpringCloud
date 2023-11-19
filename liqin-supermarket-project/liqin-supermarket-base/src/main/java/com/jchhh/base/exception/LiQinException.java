package com.jchhh.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 本项目自定义异常类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiQinException extends RuntimeException {

    private String errMessage;

    public static void cast(String message) {
        throw new LiQinException(message);
    }

    public static void cast(CommonError commonError) {
        throw new LiQinException(commonError.getErrMessage());
    }

}
