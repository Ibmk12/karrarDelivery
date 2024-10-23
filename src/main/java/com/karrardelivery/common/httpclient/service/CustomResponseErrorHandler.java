package com.karrardelivery.common.httpclient.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
public class CustomResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        boolean hasError = RestUtil.isError(clientHttpResponse.getStatusCode());
        log.debug("Checking if HTTP response has error. Status Code: {}. Error: {}",
                clientHttpResponse.getStatusCode(),
                hasError);
        return hasError;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        log.error("Response error encountered. Status Code: {}, Status Text: {}",
                clientHttpResponse.getStatusCode(),
                clientHttpResponse.getStatusText());
    }
}
