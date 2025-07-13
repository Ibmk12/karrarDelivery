package com.karrardelivery.service.impl;

import com.karrardelivery.repository.BlacklistedTokenRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistedTokenCleanupService {

    private final BlacklistedTokenRepository tokenRepository;

    @Transactional
    public void removeExpiredTokens() {
        log.info("################## TOKEN CLEANUP JOB FIRED ##################");
        Date now = new Date();
        tokenRepository.deleteAllByExpiryDateBefore(now);
        log.info("################## TOKEN CLEANUP JOB COMPLETED ##################");
    }

    // Scheduler calls this daily at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledJob() {
        removeExpiredTokens(); // Now it's safe
    }
}
