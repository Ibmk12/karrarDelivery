package com.karrardelivery.controller;

import com.karrardelivery.dto.AuthRequest;
import com.karrardelivery.dto.AuthResponse;
import com.karrardelivery.entity.management.User;
import com.karrardelivery.repository.UserRepository;
import com.karrardelivery.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getPhone(),
                        authRequest.getPassword()
                )
        );
        var accessToken = jwtUtil.generateAccessToken(authRequest.getPhone());
        var refreshToken = jwtUtil.generateRefreshToken(authRequest.getPhone());
        return new AuthResponse(accessToken, refreshToken);
    }

}