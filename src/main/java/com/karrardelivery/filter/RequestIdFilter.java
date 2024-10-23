package com.karrardelivery.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@Order(1)
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID = "requestId";
    private static final Logger logger = LoggerFactory.getLogger(RequestIdFilter.class);

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {

            if(((HttpServletRequest) request).getRequestURI().equals("/payment/execute") || ((HttpServletRequest) request).getRequestURI().equals("/payment/callBack") ||
                ((HttpServletRequest) request).getRequestURI().equals("/embedded/execute") || ((HttpServletRequest) request).getRequestURI().equals("/embedded/complete")) {

                CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest((HttpServletRequest) request);

                String requestBody = wrappedRequest.getBody();
                Map<String, Object> jsonMap = objectMapper.readValue(requestBody, Map.class);
                logger.info("Request Body: {}", jsonMap.get("originatorUuid"));

                String requestId;

                if (jsonMap.get("originatorUuid") != null) {
                    requestId = jsonMap.get("originatorUuid").toString();

                } else if (jsonMap.get("UserDefinedField") != null) {
                    requestId = jsonMap.get("UserDefinedField").toString();

                } else {
                    requestId = UUID.randomUUID().toString();
                }

                MDC.put(REQUEST_ID, requestId);
                logger.info("Incoming request {} {} with Request ID: {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI(), requestId);
                chain.doFilter(wrappedRequest, response);
            }
            else {
                chain.doFilter(request, response);
            }
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }

    @Override
    public void destroy() {
    }
}
