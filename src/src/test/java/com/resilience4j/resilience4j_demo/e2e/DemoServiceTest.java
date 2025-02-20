package com.resilience4j.resilience4j_demo.e2e;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.service.DemoService;
import com.resilience4j.resilience4j_demo.util.E2EConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.resilience4j.resilience4j_demo.util.E2EUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class DemoServiceTest {

    @Autowired
    DemoService demoService;

    @Value("${demo.server.url}")
    private String serverUrl;

    @Value("${demo.context.resource.path}")
    private String demoContextPath;

    @Value("${demo.context.resource.action}")
    private String demoAction;

    WireMockServer wireMockServer;

    @Value("${server.port}")
    private int port;

    @BeforeEach
    public void setWireMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(port));
        configureFor(E2EConstants.LOCAL_HOST, port);
        wireMockServer.start();
    }

    @AfterEach
    public void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    void whenCompletableFutureCompleted_thenExecutedOnSuccess() throws ExecutionException, InterruptedException {
        String respBody = "{\"data\":\"Success\"}";
        buildStub(wireMockServer, demoContextPath, HttpStatus.OK.value(),
                null, respBody);
        TargetServiceRequest targetServiceRequest = getTargetServiceRequest(serverUrl,
                demoAction, "12345", E2EConstants.API_NAME);
        CompletableFuture<ResponseEntity<TargetServiceResponse>> result = demoService.doDemoPostApi(targetServiceRequest);
        assertEquals(HttpStatus.OK, result.get().getStatusCode());
        Assertions.assertNotNull(result.get().getBody());
        assertEquals("Success", result.get().getBody().getData());
    }

    @Test
    void whenCompletableFutureCompleted_thenExceptionallyTimeOutExecutedOnFailure() throws RuntimeException, ExecutionException, InterruptedException {
        String respBody = "{\"data\":\"Success\"}";
        buildStubWithDelayResponse(wireMockServer, demoContextPath, HttpStatus.OK.value(),
                null, respBody);
        TargetServiceRequest targetServiceRequest = getTargetServiceRequest(serverUrl,
                demoAction, "12345", E2EConstants.API_NAME);
        CompletableFuture<ResponseEntity<TargetServiceResponse>> result = demoService.doDemoPostApi(targetServiceRequest);
        assertEquals(HttpStatus.OK, result.get().getStatusCode());
        Assertions.assertNotNull(result.get().getBody());
        assertEquals("Success", result.get().getBody().getData());
    }

    @Test
    void whenCompletableFutureCompleted_thenExceptionallyExecutedOnFailure() throws ExecutionException, InterruptedException {
        String respBody = "{\"data\":\"Failure with generic exception\"}";
        buildStub(wireMockServer, demoContextPath, 500, null, respBody);
        TargetServiceRequest targetServiceRequest = getTargetServiceRequest(serverUrl,
                demoAction, "12345", E2EConstants.API_NAME);
        CompletableFuture<ResponseEntity<TargetServiceResponse>> result = demoService.doDemoPostApi(targetServiceRequest);
        assertEquals("Failure with generic exception", result.get().getBody().getData());
        /*
        result.whenComplete((targetServiceResponseResponseEntity, throwable) -> {
            if (throwable != null) {
                System.err.println("Generic  throwable ==" + throwable.getMessage());
            }
        });*/
    }
}