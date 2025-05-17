package com.karrardelivery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraderDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String phoneNumber;

    @Email(message = "{invalid.email.error.msg}")
    private String email;

    private String description;
}
