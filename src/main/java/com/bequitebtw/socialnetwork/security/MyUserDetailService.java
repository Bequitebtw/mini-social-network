package com.bequitebtw.socialnetwork.security;

import com.bequitebtw.socialnetwork.common.exception.BadCredentialsAuthenticationException;
import com.bequitebtw.socialnetwork.domain.user.model.User;
import com.bequitebtw.socialnetwork.domain.user.repository.UserRepository;
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
	public UserDetails loadUserByUsername(@NonNull String login) throws UsernameNotFoundException {
		log.debug("Попытка найти пользователя {} в базе для идентификации", login);
		User user = userRepository.findUserByUsernameOrEmail(login, login).orElseThrow(BadCredentialsAuthenticationException::new);
		return new UserPrincipal(user);
	}
}
