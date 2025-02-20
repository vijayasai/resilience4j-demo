package com.resilience4j.resilience4j_demo.model;

public class TargetUrlInfo {

    private String contextPath;
    private String host;
    private String httpMethod;

    public TargetUrlInfo(String httpMethod, String host, String contextPath){
        this.host = host;
        this.httpMethod = httpMethod;
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getHost() {
        return host;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String toString(){
        return this.contextPath;
    }
}
