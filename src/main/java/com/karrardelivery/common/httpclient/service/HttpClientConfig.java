package com.karrardelivery.common.httpclient.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * - Supports both HTTP and HTTPS
 * - Uses a connection pool to re-use connections and save overhead of creating connections.
 * - Has a custom connection keep-alive strategy (to apply a default keep-alive if one isn't specified)
 * - Starts an idle connection monitor to continuously clean up stale connections.
 */

@Configuration
@EnableScheduling
@Slf4j
public class HttpClientConfig {

    // Connection-related configurations
    @Value("${http.connect.timeout:5000}") // Time to establish the connection with the remote host.
    private int connectTimeout;

    @Value("${http.request.timeout:10000}") // Time to wait for a connection from the connection manager/pool.
    private int requestTimeout;

    @Value("${http.socket.timeout:60000}") // Time to wait for data (a maximum period of inactivity between two consecutive data packets).
    private int socketTimeout;

    @Value("${http.max.connections:1000}") // Total number of connections in the pool.
    private int maxConnections;

    @Value("${http.max.connections.per.route:500}") // Number of connections for a specific route.
    private int maxConnectionsPerRoute;

    @Value("${http.idle.connection.wait:30}") // Time to wait before closing idle connections.
    private int idleConnectionWait;

    @Value("${http.idle.monitor.fixeddelay:5000}") // Frequency of the idle connection monitor task.
    private int idleMonitorFixedDelay;

    @Value("${http.connection.evictor.delay:5000}") // Delay between subsequent checks by the connection evictor.
    private int connectionEvictorDelay;





    @Bean(name = "poolingConnectionManager")
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        // Using custom SSL Context Builder trusting all certificates
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }

        SSLConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        poolingConnectionManager.setMaxTotal(maxConnections);
        poolingConnectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);

        return poolingConnectionManager;
    }

    @Bean(name = "httpClient")
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(requestTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(socketTimeout))
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .build();
    }
}
