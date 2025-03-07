package com.resilience4j.resilience4j_demo.service;

import com.resilience4j.resilience4j_demo.feign.DemoFeignClient;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.model.TargetUrlInfo;
import com.resilience4j.resilience4j_demo.util.DemoConstants;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

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
    @Bulkhead(name = "demo-post-api", type = Bulkhead.Type.THREADPOOL)
    @TimeLimiter(name = "demo-post-api")
    @CircuitBreaker(name = "demo-post-api", fallbackMethod = "demoFallBack")
    public CompletableFuture<ResponseEntity<TargetServiceResponse>> doDemoPostApi(TargetServiceRequest
                                                                                          targetServiceRequest)
            throws RuntimeException {

        TargetUrlInfo targetUrlInfo = new
                TargetUrlInfo(POST_HTTP_METHOD, serverUrl, demoContextPath);
            return CompletableFuture.completedFuture(demoFeignClient.
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
            System.err.println("@@ Error occurred while making target service " +exception);
            boolean b1 = exception instanceof FeignException;
            boolean b = exception instanceof TimeoutException;

            if(exception instanceof TimeoutException){
                System.err.println("*** Timeout: " + b);

                return CompletableFuture.failedFuture(new TimeoutException());
            } else if(exception instanceof FeignException){
                System.err.println("*** Feign: " + b1);
               int n =  ((FeignException) exception).status();
                return CompletableFuture.failedFuture(new RuntimeException(exception));
            }
            System.err.println("*** 111Exception: " + b1);

        return CompletableFuture.failedFuture(new RuntimeException(exception));
       // throw new RuntimeException(exception);
    }
}
