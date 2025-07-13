package com.karrardelivery.service;

import com.karrardelivery.dto.AuthDto;
import com.karrardelivery.dto.GenericResponse;

public interface AuthService {
    AuthDto refreshAccessToken(AuthDto authDto);
    GenericResponse<String> logout(String authorizationHeader, AuthDto authDto);
}
