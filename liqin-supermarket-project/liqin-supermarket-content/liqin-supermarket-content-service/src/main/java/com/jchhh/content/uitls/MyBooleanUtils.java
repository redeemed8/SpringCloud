package com.jchhh.content.uitls;

public class MyBooleanUtils {

    /**
     * 1 转换为 true
     * 0 转换为 false
     * null 还是 null
     * 其余数字也是null
     */
    public static Boolean toBoolean(Integer number) {
        if (number == null) {
            return null;
        }
        if (number == 0) {
            return Boolean.FALSE;
        }
        if (number == 1) {
            return Boolean.TRUE;
        }
        return null;
    }

}
