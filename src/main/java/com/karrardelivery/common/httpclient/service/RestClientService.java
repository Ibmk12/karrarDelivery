package com.karrardelivery.common.httpclient.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/*
Class have been upgraded to match httpClient 5 specs
 */
@Service
@Slf4j
public class RestClientService {

    private final RestTemplate syberBillerRestTemplate;

    @Autowired
    public RestClientService(RestTemplate syberBillerRestTemplate) {
        this.syberBillerRestTemplate = syberBillerRestTemplate;
    }

    /**
     * Invokes a remote service.
     *
     * @param url the service URL.
     * @param method the HTTP method.
     * @param requestEntity the request entity, including headers and body.
     * @param responseType the expected response type.
     * @param uriVariables URI variables if needed.
     * @return a ResponseEntity with the specified type.
     */
    public <T> ResponseEntity<T> invokeRemoteService(String url,
                                                     HttpMethod method,
                                                     HttpEntity<?> requestEntity,
                                                     Class<T> responseType,
                                                     Map<String, ?> uriVariables) {
        log.debug("Invoking {} to {}", method, url);
        ResponseEntity<T> response;
        if (uriVariables == null) {
            response = syberBillerRestTemplate.exchange(url, method, requestEntity, responseType);
        } else {
            response = syberBillerRestTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
        }
        log.debug("Response received with status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Invokes a service. This is an internal method that supports only GET and POST methods.
     *
     * @param url the service URL.
     * @param method the HTTP method (supports only GET and POST).
     * @param requestEntity the request entity, including headers and body.
     * @param responseType the expected response type.
     * @param uriVariables URI variables if needed.
     * @return a ResponseEntity with the specified type.
     */
    private <T> ResponseEntity<T> invokeService(String url,
                                                HttpMethod method,
                                                HttpEntity<?> requestEntity,
                                                Class<T> responseType,
                                                Map<String, ?> uriVariables) {
        log.debug("Invoking {} to {}", method, url);
        ResponseEntity<T> response;
        if (method.equals(HttpMethod.GET)) {
            response = syberBillerRestTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
        } else {
            response = syberBillerRestTemplate.postForEntity(url, requestEntity, responseType);
        }
        log.debug("Response received with status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Checks the health of the remote service.
     *
     * @param url the service URL.
     * @return a ResponseEntity with a String body indicating the health of the service.
     */
    public ResponseEntity<String> pingRemoteService(String url) {
        return invokeRemoteService(url, HttpMethod.GET, null, String.class, null);
    }

}