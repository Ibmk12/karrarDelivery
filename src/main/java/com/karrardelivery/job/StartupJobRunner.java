package com.karrardelivery.job;

import com.karrardelivery.service.impl.BlacklistedTokenCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupJobRunner {

    private final BlacklistedTokenCleanupService cleanupService;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        cleanupService.removeExpiredTokens();
    }
}
