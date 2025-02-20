package com.resilience4j.resilience4j_demo.feign;

import com.resilience4j.resilience4j_demo.exceptions.FeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DemoPostApiFeignConfigurations extends DemoFeignBaseConfigurations{

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

}
