package com.bequitebtw.socialnetwork.user.service;

import com.bequitebtw.socialnetwork.user.dto.UserShort;
import com.bequitebtw.socialnetwork.auth.model.AuthProvider;

import java.util.UUID;

public interface UserService {

    UserShort findUserById(UUID id);

    UserShort createUser(String email, String username, String password,AuthProvider authProvider);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    UserShort findUserByUsername(String username);
}
