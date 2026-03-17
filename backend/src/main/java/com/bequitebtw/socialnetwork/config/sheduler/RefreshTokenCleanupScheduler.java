package com.bequitebtw.socialnetwork.config.sheduler;

import com.bequitebtw.socialnetwork.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

	private final RefreshTokenRepository refreshTokenRepository;

	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpiredTokens() {
		refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
	}
}
