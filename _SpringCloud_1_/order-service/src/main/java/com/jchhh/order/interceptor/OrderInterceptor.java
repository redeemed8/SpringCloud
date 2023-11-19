package com.jchhh.order.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OrderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从session中获取user的信息
        String referTo = request.getHeader("ReferTo");
        // 判断用户是否符合要求
        if (!"asd4x8sdr8621dfvb4xcf65e".equals(referTo)) {
            response.setStatus(404);
            return false;
        }
        return true;
    }

}
