package com.jchhh.content.uitls;

import java.lang.reflect.Field;

public class ObjectUtils {

    /**
     * 检验一个对象属性，除了序列化参数外，是否全部为 null，全是 null返回true，有不为 null的返回 false
     *
     * @param obj 实现类
     * @return 是否全是 怒，ll
     */
    public static Boolean objFieldAllNull(Object obj) {
        if (null == obj) {
            return true;
        }
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(obj) != null && !f.getName().equals("serialVersionUID")) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
