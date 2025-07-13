package com.karrardelivery.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karrardelivery.dto.ErrorDto;
import com.karrardelivery.dto.ErrorListDto;
import com.karrardelivery.security.JwtUtil;
import com.karrardelivery.service.MessageService;
import com.karrardelivery.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

import static com.karrardelivery.constant.ErrorCodes.*;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String phone = jwtUtil.extractPhone(token);

                if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var userDetails = userDetailsService.loadUserByUsername(phone);

                    if (jwtUtil.validateToken(token)) {
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, EXPIRED_JWT_TOKEN_ERR_CODE, "jwt.token.expired", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            sendErrorResponse(response, INVALID_JWT_TOKEN_ERR_CODE, "jwt.token.invalid", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            sendErrorResponse(response, FAILED_AUTH_ERR_CODE, "jwt.auth.failed", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String messageKey, int status) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();
        String localizedMessage = messageService.getMessage(messageKey, locale);

        ErrorDto errorDto = ErrorDto.builder()
                .errorCode(String.valueOf(code))
                .errorMessage(localizedMessage)
                .build();

        ErrorListDto errorListDto = new ErrorListDto();
        errorListDto.getErrorDtoList().add(errorDto);

        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), errorListDto);
    }
}