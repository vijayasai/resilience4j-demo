package com.resilience4j.resilience4j_demo.service;

import com.resilience4j.resilience4j_demo.feign.DemoFeignClient;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.model.TargetUrlInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DemoService {

    private final DemoFeignClient demoFeignClient;

    @Autowired
    public DemoService(DemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    /**
     * @param targetServiceRequest TargetServiceRequest
     * @return CompletableFuture<ResponseEntity < TargetServiceResponse>>
     */
    @CircuitBreaker(name = "demo-post-api", fallbackMethod = "demoFallBack")
    public CompletableFuture<ResponseEntity<TargetServiceResponse>> doDemoPostApi(TargetServiceRequest targetServiceRequest) {
        TargetUrlInfo targetUrlInfo = new
                TargetUrlInfo("POST", "http://localhost:8082", "/api/v1/postDemo");
        return CompletableFuture.supplyAsync(() -> demoFeignClient.
                postDemoService(targetUrlInfo, "12345", targetServiceRequest));
    }

    /**
     * @param targetServiceRequest TargetServiceRequest
     * @param exception            Throwable
     * @return CompletableFuture<ResponseEntity < TargetServiceResponse>>
     * @throws RuntimeException Exception
     */
    public CompletableFuture<ResponseEntity<TargetServiceResponse>> demoFallBack(
            TargetServiceRequest targetServiceRequest,
            Throwable exception) throws RuntimeException {
        System.err.println("Error occurred while making target service " + targetServiceRequest.getMethodName()
                + " for the trackId: " + targetServiceRequest.getMethodName());
        throw new RuntimeException(exception);
    }
}
