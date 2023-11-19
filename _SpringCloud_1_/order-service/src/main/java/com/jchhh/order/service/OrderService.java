package com.jchhh.order.service;

import com.jchhh.feign.clients.UserClient;
import com.jchhh.feign.pojo.User;
import com.jchhh.order.mapper.OrderMapper;
import com.jchhh.order.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserClient userClient;          //  基于 Feign 远程调用

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            return null;
        }
        //  2.用 Feign进行远程调用
        User user = userClient.findById(order.getUserId());
        //  3. 封装 user到 order
        order.setUser(user);
        // 4.返回
        return order;
    }

//    @Autowired
//    private RestTemplate restTemplate;        //  基于 restTemplate 远程调用
//
//    public Order queryOrderById(Long orderId) {
//        // 1.查询订单
//        Order order = orderMapper.findById(orderId);
//        //  2.利用 RestTemplate发起 http请求，查询用户
//        //  2.1 url路径
//        if (order == null) {
//            return null;
//        }
//        String url = "http://userservice/user/" + order.getUserId();
//        //  2.2 发送 http请求，实现远程调用
//        User user = restTemplate.getForObject(url, User.class);
//        //  3. 封装 user到 order
//        order.setUser(user);
//        // 4.返回
//        return order;
//    }

}
