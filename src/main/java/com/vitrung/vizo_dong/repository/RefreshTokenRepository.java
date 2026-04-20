package com.vitrung.vizo_dong.repository;

import com.vitrung.vizo_dong.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUsername(String username);
    void deleteByToken(String token);
}
