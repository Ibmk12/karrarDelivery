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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getPhone(),
                        authRequest.getPassword()
                )
        );
        var accessToken = jwtUtil.generateAccessToken(authRequest.getPhone());
        var refreshToken = jwtUtil.generateRefreshToken(authRequest.getPhone()); // you can generate it differently
        return new AuthResponse(accessToken, refreshToken);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) {
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return ResponseEntity.badRequest().body("Phone number already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Secure password
        user.setRole(request.getRole());
        user.setEnabled(true);
        user.setDeleted(false);

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }


}