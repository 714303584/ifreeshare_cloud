package com.ifreeshare.web.conf;

import com.ifreeshare.web.interceptor.CustomerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private CustomerInterceptor customerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("addInterceptors");
        registry.addInterceptor(customerInterceptor).addPathPatterns("/customer/**");
    }
}
