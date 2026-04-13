package com.vitrung.vizo_dong.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
