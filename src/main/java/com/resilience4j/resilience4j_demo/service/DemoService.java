package com.resilience4j.resilience4j_demo.service;

import com.resilience4j.resilience4j_demo.feign.DemoFeignClient;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.model.TargetUrlInfo;
import com.resilience4j.resilience4j_demo.util.DemoConstants;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.resilience4j.resilience4j_demo.util.DemoConstants.POST_HTTP_METHOD;

@Service
public class DemoService {

    private final DemoFeignClient demoFeignClient;

    @Value("${demo.server.url}")
    private String serverUrl;

    @Value("${demo.context.resource.path}")
    private String demoContextPath;

    @Value("${demo.context.resource.action}")
    private String demoAction;

    @Autowired
    public DemoService(DemoFeignClient demoFeignClient) {
        this.demoFeignClient = demoFeignClient;
    }

    /**
     * @param targetServiceRequest TargetServiceRequest
     * @return CompletableFuture<ResponseEntity < TargetServiceResponse>>
     */
    @TimeLimiter(name = DemoConstants.FEIGN_NAME)
    @Bulkhead(name =DemoConstants.FEIGN_NAME, type = Bulkhead.Type.THREADPOOL)
    @CircuitBreaker(name = DemoConstants.FEIGN_NAME, fallbackMethod = "demoFallBack")
    public CompletableFuture<ResponseEntity<TargetServiceResponse>> doDemoPostApi(TargetServiceRequest
                                                                                              targetServiceRequest) throws RuntimeException {
        TargetUrlInfo targetUrlInfo = new
                TargetUrlInfo(POST_HTTP_METHOD, serverUrl, demoContextPath);

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
