package com.bequitebtw.socialnetwork.domain.user.service;

import com.bequitebtw.socialnetwork.domain.user.model.User;

public interface UserService {
	User save(User user);

	User createUser(String email, String username, String password);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	User findUserProfileByUsername(String username);
}
