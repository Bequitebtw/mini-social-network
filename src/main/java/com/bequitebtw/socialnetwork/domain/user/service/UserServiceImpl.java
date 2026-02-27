package com.bequitebtw.socialnetwork.domain.user.service;

import com.bequitebtw.socialnetwork.domain.user.model.User;
import com.bequitebtw.socialnetwork.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public User createUser(String email, String username, String passwordHash) {
		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(passwordHash);
		userRepository.save(user);
		return user;
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
	public User findUserProfileByUsername(String username) {
		return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
	}

}
