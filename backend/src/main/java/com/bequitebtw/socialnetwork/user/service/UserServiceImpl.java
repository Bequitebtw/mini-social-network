package com.bequitebtw.socialnetwork.user.service;

import com.bequitebtw.socialnetwork.common.exception.UserNotFoundException;
import com.bequitebtw.socialnetwork.user.dto.UserShort;
import com.bequitebtw.socialnetwork.user.mapper.UserMapper;
import com.bequitebtw.socialnetwork.auth.model.AuthProvider;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.auth.model.UserAuthProvider;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserShort findUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toShort(user);
    }

    @Override
    public UserShort createUser(String email, String username, String passwordHash, AuthProvider authProvider) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        User createdUser = userRepository.save(user);

        UserAuthProvider userAuthProvider = new UserAuthProvider();
        userAuthProvider.setProvider(AuthProvider.LOCAL);
        userAuthProvider.setPassword(passwordHash);
        userAuthProvider.setUser(createdUser);

        createdUser.getProviders().add(userAuthProvider);
        userRepository.save(createdUser);
        log.info("Account with email {} and username {} created successfully", createdUser.getEmail(), createdUser.getUsername());

        return userMapper.toShort(createdUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public UserShort findUserByUsername(String username) {
        User user = userRepository.findUserByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return userMapper.toShort(user);
    }

}
