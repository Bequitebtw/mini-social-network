package com.bequitebtw.socialnetwork.security;

import com.bequitebtw.socialnetwork.domain.user.model.User;
import com.bequitebtw.socialnetwork.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		return new UserPrincipal(user);
	}
}
