package com.resilience4j.resilience4j_demo.exceptions;

import org.springframework.stereotype.Component;

@Component
public class DemoExceptionHelper {

    public static String errorMessage(){
        return "error";
    }
}
