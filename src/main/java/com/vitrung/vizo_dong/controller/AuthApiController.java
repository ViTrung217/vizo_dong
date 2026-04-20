package com.vitrung.vizo_dong.controller;

import com.vitrung.vizo_dong.dto.AuthResponseDto;
import com.vitrung.vizo_dong.dto.LoginRequestDto;
import com.vitrung.vizo_dong.dto.RefreshTokenRequestDto;
import com.vitrung.vizo_dong.dto.RegisterRequestDto;
import com.vitrung.vizo_dong.entity.RefreshToken;
import com.vitrung.vizo_dong.service.CustomUserDetailsService;
import com.vitrung.vizo_dong.service.JwtService;
import com.vitrung.vizo_dong.service.RefreshTokenService;
import com.vitrung.vizo_dong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        if (registerRequestDto.getUsername() == null || registerRequestDto.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username không được để trống"));
        }
        if (registerRequestDto.getEmail() == null || registerRequestDto.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được để trống"));
        }
        if (registerRequestDto.getPassword() == null || !registerRequestDto.getPassword().equals(registerRequestDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu xác nhận không khớp"));
        }
        try {
            userService.register(registerRequestDto);
            return ResponseEntity.ok(Map.of("message", "Đăng ký thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username/password không được để trống"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshTokenRaw = jwtService.generateRefreshToken(userDetails.getUsername());
            refreshTokenService.createOrRotate(userDetails.getUsername(), refreshTokenRaw);

            return ResponseEntity.ok(new AuthResponseDto("Bearer", accessToken, refreshTokenRaw));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Sai username hoặc password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi hệ thống khi đăng nhập"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDto request) {
        try {
            RefreshToken savedRefreshToken = refreshTokenService.verifyUsable(request.getRefreshToken());
            String username = jwtService.extractUsername(savedRefreshToken.getToken());

            if (jwtService.isTokenExpired(savedRefreshToken.getToken())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Refresh token đã hết hạn"));
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(username);
            refreshTokenService.createOrRotate(username, newRefreshToken);

            return ResponseEntity.ok(new AuthResponseDto("Bearer", newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDto request) {
        refreshTokenService.revoke(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }
}
