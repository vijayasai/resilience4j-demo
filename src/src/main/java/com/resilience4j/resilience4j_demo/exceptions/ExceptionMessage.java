package com.resilience4j.resilience4j_demo.exceptions;

public record ExceptionMessage(String timestamp,
                               int status,
                               String error,
                               String message,
                               String path) {
}

