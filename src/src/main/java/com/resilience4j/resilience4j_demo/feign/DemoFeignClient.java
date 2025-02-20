package com.resilience4j.resilience4j_demo.feign;

import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.model.TargetUrlInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.resilience4j.resilience4j_demo.util.DemoConstants.*;

@FeignClient(name = DEMO_API_SERVICE, url = "${demo.server.url}", configuration = DemoPostApiFeignConfigurations.class)
public interface DemoFeignClient {

    @PostMapping(value = "${demo.context.resource.path}")
    ResponseEntity<TargetServiceResponse> postDemoService(
            @RequestHeader(REFERER) TargetUrlInfo targetUrlInfo,
            @RequestHeader(TRACK_ID) String trackId,
            @RequestBody TargetServiceRequest targetServiceRequest);

}
