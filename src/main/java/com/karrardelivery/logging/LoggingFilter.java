package com.karrardelivery.logging;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A filter responsible for logging incoming HTTP requests and their corresponding responses.
 * Excludes specific URIs from logging, e.g., '/status' and URIs containing 'swagger'.
 */
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_PREFIX = "Request: ";
    private static final String RESPONSE_PREFIX = "Response: ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String requestUri = request.getRequestURI();

        RequestLoggingWrapper requestLoggingWrapper = new RequestLoggingWrapper(startTime, request);
        ResponseLoggingWrapper responseLoggingWrapper = new ResponseLoggingWrapper(startTime, response);

        // Log incoming request if applicable
        if (!shouldSkipLogging(requestUri)) {
            logRequest(requestLoggingWrapper);
        }

        try {
            filterChain.doFilter(requestLoggingWrapper, responseLoggingWrapper);
        } finally {
            // Log response if applicable
            if (!shouldSkipLogging(requestUri)) {
                logResponse(responseLoggingWrapper);
                log.debug("{}: http response {} finished in {} ms", startTime, response.getStatus(), System.currentTimeMillis() - startTime);
            }
        }
    }

    /**
     * Determines if the given request URI should be excluded from logging.
     *
     * @param requestUri The URI of the incoming request.
     * @return True if the URI should skip logging, otherwise false.
     */
    private boolean shouldSkipLogging(String requestUri) {
        return "/status".equals(requestUri) || requestUri.contains("swagger");
    }

    /**
     * Constructs and logs a detailed message about the incoming request.
     *
     * @param request The HTTP request to be logged.
     */
    private void logRequest(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder(REQUEST_PREFIX);

        if (request instanceof RequestLoggingWrapper) {
            msg.append("request id=").append(((RequestLoggingWrapper) request).getId()).append("; ");
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            msg.append("session id=").append(session.getId()).append("; ");
        }

        if (request.getContentType() != null) {
            msg.append("content type=").append(request.getContentType()).append("; ");
        }

        msg.append("method=").append(request.getMethod()).append("; ");
        msg.append("uri=").append(request.getRequestURI());

        if (request.getQueryString() != null) {
            msg.append('?').append(request.getQueryString());
        }

        if (request instanceof RequestLoggingWrapper && !isMultipart(request)) {
            RequestLoggingWrapper requestLoggingWrapper = (RequestLoggingWrapper) request;
            try {
                String charEncoding = requestLoggingWrapper.getCharacterEncoding() != null ? requestLoggingWrapper.getCharacterEncoding() : "UTF-8";
                String requestData = new String(requestLoggingWrapper.toByteArray(), charEncoding);
                msg.append("; payload=").append(requestData);
            } catch (UnsupportedEncodingException e) {
                log.warn("Failed to parse request payload", e);
            }
        }

        log.debug(msg.toString());
    }

    /**
     * Constructs and logs a detailed message about the outgoing response.
     *
     * @param response The HTTP response to be logged.
     */
    private void logResponse(ResponseLoggingWrapper response) {
        StringBuilder msg = new StringBuilder(RESPONSE_PREFIX);
        msg.append("request id=").append(response.getId());

        if (!isBinaryContent(response)) {
            try {
                msg.append("; payload=").append(new String(response.toByteArray(), response.getCharacterEncoding()));
            } catch (UnsupportedEncodingException e) {
                log.warn("Failed to parse response payload", e);
            }
        }

        log.debug(msg.toString());
    }

    private boolean isBinaryContent(HttpServletResponse response) {
        if (response.getContentType() == null) {
            return false;
        }
        return response.getContentType().startsWith("image") || response.getContentType().startsWith("video") || response.getContentType().startsWith("audio");
    }

    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }
}