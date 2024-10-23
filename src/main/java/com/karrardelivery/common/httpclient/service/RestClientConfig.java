package com.karrardelivery.common.httpclient.service;


import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * - Supports both HTTP and HTTPS
 * - Uses a connection pool to re-use connections and save overhead of creating connections.
 * - Has a custom connection keep-alive strategy (to apply a default keep-alive if one isn't specified)
 * - Starts an idle connection monitor to continuously clean up stale connections.
 */
@Configuration
public class RestClientConfig {

    @Autowired
    @Qualifier("httpClient")
    CloseableHttpClient syberBillerHttpClient;

    @Primary
    @Bean
    public HttpComponentsClientHttpRequestFactory syberClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(syberBillerHttpClient);
        return clientHttpRequestFactory;
    }



    @Primary
    @Bean(name = "cdrRestTemplate")
    public RestTemplate syberRestTemplate() {
        return new RestTemplate(syberClientHttpRequestFactory());
    }

}

