package com.karrardelivery.service.impl;

import com.karrardelivery.dto.AuthDto;
import com.karrardelivery.repository.UserRepository;
import com.karrardelivery.security.JwtUtil;
import com.karrardelivery.service.AuthService;
import com.karrardelivery.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final MessageService messageService;

    @Override
    public AuthDto refreshAccessToken(AuthDto authDto) {

        if (authDto.getRefreshToken() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageService.getMessage("auth.missing.refresh.token"));
        }

        String refreshToken = authDto.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    messageService.getMessage("auth.invalid.refresh.token"));
        }

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageService.getMessage("auth.not.refresh.token"));
        }

        String phone = jwtUtil.extractPhone(refreshToken);

        var user = userRepository.findByPhoneAndDeletedFalse(phone)
                .filter(u -> u.isEnabled())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        messageService.getMessage("user.not.found")));

        String newAccessToken = jwtUtil.generateAccessToken(phone);
        return new AuthDto(newAccessToken, refreshToken);
    }
}
