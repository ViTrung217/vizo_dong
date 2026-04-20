package com.vitrung.vizo_dong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequestDto {
    private String email;
    private String newPassword;
    private String confirmPassword;
}
