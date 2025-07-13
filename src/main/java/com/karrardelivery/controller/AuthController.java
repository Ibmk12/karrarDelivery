package com.karrardelivery.controller;

import com.karrardelivery.dto.LoginRequest;
import com.karrardelivery.dto.AuthDto;
import com.karrardelivery.security.JwtUtil;
import com.karrardelivery.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.karrardelivery.constant.ApiUrls.*;

@RestController
@RequestMapping(AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping(LOGIN)
    public AuthDto login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getPhone(),
                        loginRequest.getPassword()
                )
        );
        var accessToken = jwtUtil.generateAccessToken(loginRequest.getPhone());
        var refreshToken = jwtUtil.generateRefreshToken(loginRequest.getPhone());
        return new AuthDto(accessToken, refreshToken);
    }

    @PostMapping(REFRESH)
    public ResponseEntity<AuthDto> refreshToken(@RequestBody AuthDto authDto) {
        return ResponseEntity.ok(authService.refreshAccessToken(authDto));
    }


}