package com.resilience4j.resilience4j_demo.feign;

import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.model.TargetUrlInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "DEMO-APIService", url = "http://localhost:8082")
public interface DemoFeignClient {

    @PostMapping(value = "/api/v1/postDemo")
    ResponseEntity<TargetServiceResponse> postDemoService(
            @RequestHeader("Referer") TargetUrlInfo targetUrlInfo,
            @RequestHeader("trackId") String trackId,
            @RequestBody TargetServiceRequest targetServiceRequest);

}
