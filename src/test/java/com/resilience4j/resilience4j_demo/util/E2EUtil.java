package com.resilience4j.resilience4j_demo.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.http.MediaType;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class E2EUtil {

    /**
     *
     * @param wireMockServer WireMockServer
     * @param endPoint String
     * @param httpStatusCode int
     * @param requestBody String
     * @param responseBody String
     */
    public static void buildStub(WireMockServer wireMockServer,
                                       String endPoint,
                                       int httpStatusCode,
                                       String requestBody,
                                       String responseBody) {
        wireMockServer.stubFor(post(urlEqualTo(endPoint))
                        .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                        //.withRequestBody(WireMock.equalToJson(requestBody))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(httpStatusCode)
                                .withBody(responseBody)));
    }

    /**
     *
     * @param wireMockServer WireMockServer
     * @param endPoint String
     * @param httpStatusCode int
     * @param requestBody String
     * @param responseBody String
     */
    public static void buildStubWithDelayResponse(WireMockServer wireMockServer,
                                       String endPoint,
                                       int httpStatusCode,
                                       String requestBody,
                                       String responseBody) {
        wireMockServer.stubFor(post(urlEqualTo(endPoint))
                        .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                        //.withRequestBody(WireMock.equalToJson(requestBody))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withFixedDelay(7000)
                                .withStatus(httpStatusCode)
                                .withBody(responseBody)));
    }

    /**
     *
     * @param feignName String
     * @return TimeLimiter
     */
    public static void setTimeLimiter(String feignName, long timeInMillis) {
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig
                .custom()
                .timeoutDuration(Duration.ofMillis(timeInMillis))
                .build();
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(timeLimiterConfig);
        timeLimiterRegistry.timeLimiter(feignName);
    }

    /**
     *
     * @param targetUri String
     * @param httpMethod String
     * @param traceId String
     * @param apiName String
     * @return TargetServiceRequest
     */
    public static TargetServiceRequest getTargetServiceRequest(String targetUri, String httpMethod, String traceId,
                                                               String apiName) {
        TargetServiceRequest targetServiceRequest = new TargetServiceRequest();
        targetServiceRequest.setTargetUri(targetUri);
        targetServiceRequest.setHttpMethod(httpMethod);
        targetServiceRequest.setTrackId(traceId);
        targetServiceRequest.setMethodName(apiName);
        return targetServiceRequest;
    }
}
