package com.bequitebtw.socialnetwork.user.service;

import com.bequitebtw.socialnetwork.common.exception.UserNotFoundException;
import com.bequitebtw.socialnetwork.user.dto.UserShort;
import com.bequitebtw.socialnetwork.user.mapper.UserMapper;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public UserShort save(User user) {
		return userMapper.toShort(userRepository.save(user));
	}

	@Override
	public UserShort findUserById(UUID id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
		System.out.println(user.getEmail());
		return userMapper.toShort(user);
	}

	@Override
	public UserShort createUser(String email, String username, String passwordHash) {
		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(passwordHash);
		return userMapper.toShort(userRepository.save(user));
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public UserShort findUserByUsername(String username) {
		User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
		return userMapper.toShort(user);
	}

}
