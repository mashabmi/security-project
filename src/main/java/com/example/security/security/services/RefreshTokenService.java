package com.example.security.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.security.exception.TokenRefreshException;
import com.example.security.model.RefreshToken;
import com.example.security.repository.RefreshTokenRepository;
import com.example.security.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Value("${spring.application.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);
        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            // Update the existing refresh token
            refreshToken = existingToken.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        } else {
            // Create a new refresh token
            refreshToken = new RefreshToken();
            refreshToken.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        }
        
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
