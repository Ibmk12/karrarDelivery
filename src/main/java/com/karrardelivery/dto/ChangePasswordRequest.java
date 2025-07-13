package com.karrardelivery.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String phone;
    private String oldPassword;
    private String newPassword;
}
