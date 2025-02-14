package com.resilience4j.resilience4j_demo.e2e;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.service.DemoService;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.resilience4j.resilience4j_demo.util.E2EUtil.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
public class DemoServiceTest {

    @Autowired
    DemoService demoService;

    WireMockServer wireMockServer;

    @BeforeEach
    public void setWireMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8082));
        configureFor("localhost", 8082);
        wireMockServer.start();
    }

    @AfterEach
    public void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    void postDemoTest200() {
        String respBody = "{\"data\":\"Success\"}";
       /* wireMockServer.stubFor(post(urlEqualTo("/api/v1/postDemo"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                      //  .withFixedDelay(7000)
                        .withStatus(HttpStatus.OK.value()).withBody(respBody))).toString();*/

        buildStub(wireMockServer, "/api/v1/postDemo", HttpStatus.OK.value(),
                null, respBody );
        try {
            CompletableFuture<ResponseEntity<TargetServiceResponse>> result = demoService.doDemoPostApi(getTargetServiceRequest());

            result.whenComplete((targetServiceResponseResponseEntity, throwable) -> {
                if (throwable != null) {
                    throw new RuntimeException(throwable);
                }
                if (targetServiceResponseResponseEntity != null) {
                    System.out.println("*********SUCCESS*******"+targetServiceResponseResponseEntity.getBody().getData());
                }
            });
            int m = result.get().getStatusCode().value();
            System.out.println("m=" + m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void postDemoTimeOutTest() {
        String respBody = "{\"data\":\"Success\"}";


        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig
                .custom()
                .timeoutDuration(Duration.ofMillis(1))
                .build();
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(timeLimiterConfig);
        TimeLimiter tl = timeLimiterRegistry.timeLimiter("demo-post-api");

        buildStubWithDelayResponse(wireMockServer, "/api/v1/postDemo", HttpStatus.OK.value(),null, respBody );


        try {
            CompletableFuture<ResponseEntity<TargetServiceResponse>> result = demoService.doDemoPostApi(getTargetServiceRequest());

            /*result.whenComplete((targetServiceResponseResponseEntity, throwable) -> {
                if (throwable != null) {
                  //  throw new RuntimeException(throwable);
                    System.err.println("*****Timeout exception ::"+throwable.getMessage());
                }
                if (targetServiceResponseResponseEntity != null) {
                    System.out.println("*********SUCCESS*******"+targetServiceResponseResponseEntity.getBody().getData());
                }
            });*/
            int m = result.get().getStatusCode().value();
            System.out.println("****************m=" + m);
        } catch (Exception e) {
            e.printStackTrace();
        }
      //  setTimeLimiter("demo-post-api", 36000);
    }

    private TargetServiceRequest getTargetServiceRequest() {
        TargetServiceRequest targetServiceRequest = new TargetServiceRequest();
        targetServiceRequest.setHttpMethod("PUT");
        targetServiceRequest.setTargetUri("http://localhost:8082");
        targetServiceRequest.setTrackId("12345");
        targetServiceRequest.setMethodName("TestVijay");
        return targetServiceRequest;
    }
}
