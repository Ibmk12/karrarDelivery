package com.karrardelivery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppSetting {

    private final static String HTTP_CLIENT_MAXIMUM_TOTAL_CONNECTIONS = "httpclient.maximum.total.connections";

    private final static String HTTP_CLIENT_MAXIMUM_CONCURRENT_CONNECTIONS = "httpclient.maximum.concurrent.connections";

    private final static String CONNECT_TIME_OUT = "connect.timeout";
    private final static String READ_TIME_OUT = "read.timeout";


    @Autowired
    private Environment env;

    public Integer getMaximumTotalConnections() {
        return env.getProperty(HTTP_CLIENT_MAXIMUM_TOTAL_CONNECTIONS, Integer.class, 50);
    }

    public Integer getMaximumConcurrentConnections() {
        return env.getProperty(HTTP_CLIENT_MAXIMUM_CONCURRENT_CONNECTIONS, Integer.class, 5);
    }

    public Integer getConnectTimeOut() {
        return env.getProperty(CONNECT_TIME_OUT, Integer.class, 25000);
    }

    public Integer getReadTimeOut() {
        return env.getProperty(READ_TIME_OUT, Integer.class, 150000);
    }

}
