package com.karrardelivery.service;

import com.karrardelivery.dto.AuthDto;

public interface AuthService {
    AuthDto refreshAccessToken(AuthDto authDto);
}
