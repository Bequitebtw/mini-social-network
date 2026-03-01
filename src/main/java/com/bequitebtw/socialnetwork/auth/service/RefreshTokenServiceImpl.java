package com.bequitebtw.socialnetwork.auth.service;

import com.bequitebtw.socialnetwork.common.exception.TokenNotFoundException;
import com.bequitebtw.socialnetwork.auth.model.RefreshToken;
import com.bequitebtw.socialnetwork.auth.repository.RefreshTokenRepository;
import com.bequitebtw.socialnetwork.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public RefreshToken save(RefreshToken refreshToken) {
		return refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken findByToken(String token) {
		return refreshTokenRepository.findByToken(token).orElseThrow(TokenNotFoundException::new);
	}

	@Override
	public boolean existByToken(String token) {
		return refreshTokenRepository.existsByToken(token);
	}

	@Override
	public void deleteByUser(User user) {
		refreshTokenRepository.deleteByUser(user);
	}
}
