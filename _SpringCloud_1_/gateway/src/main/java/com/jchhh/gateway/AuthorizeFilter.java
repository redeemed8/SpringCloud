package com.jchhh.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-1)
@Component
public class AuthorizeFilter implements GatewayFilter, Ordered {

    //  过滤器的执行顺序
    //  defaultFilter > 路由过滤器 > globalFilter 的顺序执行

    //  在这里这个没用，因为在 application.yml中配置了 defaultFilter

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        //  获取请求参数
//        ServerHttpRequest request = exchange.getRequest();
//        HttpHeaders headers = request.getHeaders();
//        //  获取参数中的 ReferTo 参数
//        String referTo = headers.getFirst("ReferTo");
//        //  判断参数值是否等于 asd4x8sdr8621dfvb4xcf65e,这个按理说，应该每次随机生成
//        if ("asd4x8sdr8621dfvb4xcf65e".equals(referTo)) {
//            //  是，放行
//            return chain.filter(exchange);
//        }
//        //  否，拦截
//        //  设置状态码
//        exchange.getResponse().setStatusCode(HttpStatus.valueOf(404));
//        //  进行请求的拦截
//        return exchange.getResponse().setComplete();

        System.out.println("进入到全局过滤器了........");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
