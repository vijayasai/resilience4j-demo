package com.resilience4j.resilience4j_demo.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

 private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message = null;
        try (InputStream body = response.body().asInputStream()){
            ;
           // message = new ExceptionMessage((String) response.headers().get("date").toArray()[0],
            message = new ExceptionMessage( new Date().toString(),
                    response.status(),
                    HttpStatus.resolve(response.status()).getReasonPhrase(),
                    IOUtils.toString(body, StandardCharsets.UTF_8),
                    response.request().url());

        } catch (IOException exception) {
            return new Exception(exception.getMessage());
        }
        switch (response.status()) {
            case 404:
                return new Exception(message.message());
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

}