package com.resilience4j.resilience4j_demo.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.resilience4j.resilience4j_demo.model.TargetServiceRequest;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.http.MediaType;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class E2EConstants {

    public static final String LOCAL_HOST = "localhost";
    public static final String API_NAME = "Demo API";
    public static final String FEIGN_NAME ="demo-post-api";
}
