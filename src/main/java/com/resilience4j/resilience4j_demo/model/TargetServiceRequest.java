package com.resilience4j.resilience4j_demo.model;

import java.util.List;

public class TargetServiceRequest {

    private String httpMethod;
    private String data;
    private String targetUri;
    private List<RequestParams> uriParams;
    private List<RequestParams> queryParams;
    private String methodName;
    private String trackId;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public List<RequestParams> getUriParams() {
        return uriParams;
    }

    public void setUriParams(List<RequestParams> uriParams) {
        this.uriParams = uriParams;
    }

    public List<RequestParams> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<RequestParams> queryParams) {
        this.queryParams = queryParams;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
}
