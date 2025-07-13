package com.karrardelivery.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private boolean enabled;
    private String role;
}
