package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * create by liufeng
 * restTemplate默认不支持patch
 * 需要配置
 */
@Component
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new ThrowErrorHandler());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
//       SimpleClientHttpRequestFactory factory=new SimpleClientHttpRequestFactory();
//      上一行被注释掉的是Spring自己的实现，下面是依赖了httpclient包后的实现
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(300000);
        factory.setReadTimeout(300000);
        return factory;
    }
}