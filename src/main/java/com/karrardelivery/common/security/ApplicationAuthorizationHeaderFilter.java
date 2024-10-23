package com.karrardelivery.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.karrardelivery.common.utility.Constants;
import com.karrardelivery.model.BaseRequestResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.Writer;

@Slf4j
//@Component(value = "authorizationHeaderFilter")
//@Order(2)
public class ApplicationAuthorizationHeaderFilter extends OncePerRequestFilter {

    @Value("${gateway.authentication.api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {

        if ((httpServletRequest.getRequestURI().equals("/status")))
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        else {
            logger.trace("in AuthorizationHeaderFilter");

            boolean isValidKey = false;
            String xAuth = httpServletRequest.getHeader("Authorization");
            if(xAuth!=null && apiKey!=null)
            {
                if(xAuth.trim().equalsIgnoreCase(apiKey.trim()))
                    isValidKey=true;
            }

            if (isValidKey) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } else {
                log.error("Unauthorized application");
                BaseRequestResponse authorizationException = new BaseRequestResponse();

                httpServletResponse.setContentType("application/json;charset=UTF-8");
                try (Writer out = httpServletResponse.getWriter()) {
                    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    authorizationException.setResponseCode(Constants.UNAUTHORIZED_CODE);
                    authorizationException.setResponseMessage(Constants.UNAUTHORIZED_MESSAGE);
                    authorizationException.setStatus("Failed");
                    String json = ow.writeValueAsString(authorizationException);
                    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                    out.write(json);
                } catch (Exception e) {
                    logger.error("Failed to write to Authentication Failure response", e);
                    httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication Failure processing failed");
                }
            }
        }
    }
}