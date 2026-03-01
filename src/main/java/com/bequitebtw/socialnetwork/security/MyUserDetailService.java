package com.bequitebtw.socialnetwork.security;

import com.bequitebtw.socialnetwork.common.exception.BadCredentialsAuthenticationException;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	@NonNull
	@Transactional
	public UserDetails loadUserByUsername(@NonNull String login) throws UsernameNotFoundException {
		log.info("Попытка найти пользователя {} в базе для авторизации", login);
		User user = userRepository.findUserByUsernameOrEmail(login, login).orElseThrow(BadCredentialsAuthenticationException::new);
		return new UserPrincipal(user);
	}
}
