package com.bs.linkedinmicroservices.userService.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email, password;
}
