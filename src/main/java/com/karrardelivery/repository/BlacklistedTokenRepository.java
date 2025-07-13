package com.karrardelivery.repository;

import com.karrardelivery.entity.management.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByAccessToken(String token);
    Optional<BlacklistedToken> findByRefreshToken(String token);
    void deleteAllByExpiryDateBefore(Date date);
}
