package com.resilience4j.resilience4j_demo.e2e;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import com.resilience4j.resilience4j_demo.model.TargetServiceResponse;
import com.resilience4j.resilience4j_demo.service.DemoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@ActiveProfiles("test")
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
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/postDemo"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value()).withBody(respBody))).toString();

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

    private TargetServiceRequest getTargetServiceRequest() {
        TargetServiceRequest targetServiceRequest = new TargetServiceRequest();
        targetServiceRequest.setHttpMethod("PUT");
        targetServiceRequest.setTargetUri("http://localhost:8082");
        targetServiceRequest.setTrackId("12345");
        targetServiceRequest.setMethodName("TestVijay");
        return targetServiceRequest;
    }
}
