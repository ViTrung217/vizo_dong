package com.vitrung.vizo_dong.service;

import com.vitrung.vizo_dong.config.JwtProperties;
import com.vitrung.vizo_dong.entity.RefreshToken;
import com.vitrung.vizo_dong.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Transactional
    public RefreshToken createOrRotate(String username, String token) {
        refreshTokenRepository.deleteByUsername(username);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(token);
        refreshToken.setRevoked(false);
        refreshToken.setExpiredAt(LocalDateTime.now().plusNanos(jwtProperties.getRefreshExpirationMs() * 1_000_000));
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyUsable(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }
        if (refreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token đã hết hạn");
        }
        return refreshToken;
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(savedToken -> {
            savedToken.setRevoked(true);
            refreshTokenRepository.save(savedToken);
        });
    }
}
