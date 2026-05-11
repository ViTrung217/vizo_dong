package com.vitrung.vizo_dong.dto;

import com.vitrung.vizo_dong.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserUpdateRequestDto {
    private String email;
    private UserRole role;
    private String newPassword;
    private String confirmPassword;
}