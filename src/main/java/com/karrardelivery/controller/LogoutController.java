package com.karrardelivery.controller;

import com.karrardelivery.dto.AuthDto;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.karrardelivery.constant.ApiUrls.*;

@RestController
@RequestMapping(LOGOUT)
@RequiredArgsConstructor
@Slf4j
public class LogoutController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<GenericResponse<String>> logout(@RequestHeader("Authorization") String authHeader, @RequestBody AuthDto authDto) {
        return ResponseEntity.ok(authService.logout(authHeader, authDto));
    }

}