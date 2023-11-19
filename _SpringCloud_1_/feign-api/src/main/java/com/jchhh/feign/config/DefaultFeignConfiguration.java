package com.jchhh.feign.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfiguration implements RequestInterceptor {

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.BASIC;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("ReferTo", "asd4x8sdr8621dfvb4xcf65e");
    }
}
