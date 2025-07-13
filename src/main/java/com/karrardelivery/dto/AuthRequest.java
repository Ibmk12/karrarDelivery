package com.karrardelivery.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {
    private String phone;
    private String password;
}