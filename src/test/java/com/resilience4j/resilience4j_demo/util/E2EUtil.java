package com.resilience4j.resilience4j_demo.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.http.MediaType;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class E2EUtil {

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
                                .withBody(responseBody)))
                .toString();
    }

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
                                .withBody(responseBody)))
                .toString();
    }

    /**
     *
     * @param feignName String
     * @return TimeLimiter
     */
    public static TimeLimiter setTimeLimiter(String feignName, long timeInMillis) {
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig
                .custom()
                .timeoutDuration(Duration.ofSeconds(timeInMillis))
                .build();
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(timeLimiterConfig);
        return timeLimiterRegistry.timeLimiter(feignName);
    }
}
