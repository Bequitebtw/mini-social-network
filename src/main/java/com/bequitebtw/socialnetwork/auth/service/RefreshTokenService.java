package com.bequitebtw.socialnetwork.auth.service;

import com.bequitebtw.socialnetwork.auth.model.RefreshToken;
import com.bequitebtw.socialnetwork.user.model.User;


public interface RefreshTokenService {
	RefreshToken save(RefreshToken refreshToken);

	RefreshToken findByToken(String token);

	boolean existByToken(String token);

	void deleteByUser(User user);

	void deleteByToken(String token);
}
