package com.resilience4j.resilience4j_demo.feign;

import com.resilience4j.resilience4j_demo.exceptions.FeignErrorDecoder;
import feign.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


public class DemoFeignBaseConfigurations {

    @Value("${logging.feign.log.level}")
    private Logger.Level loggingLevel;

    @Bean
    public Logger.Level feignLoggerLevel(){
        return loggingLevel;
    }

    @Bean
    public FeignErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
