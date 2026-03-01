package com.bequitebtw.socialnetwork.user.service;

import com.bequitebtw.socialnetwork.user.dto.UserShort;
import com.bequitebtw.socialnetwork.user.model.User;

import java.util.UUID;

public interface UserService {
	UserShort save(User user);

	UserShort findUserById(UUID id);

	UserShort createUser(String email, String username, String password);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	UserShort findUserByUsername(String username);
}
