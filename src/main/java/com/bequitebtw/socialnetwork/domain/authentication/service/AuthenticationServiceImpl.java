package com.bequitebtw.socialnetwork.domain.authentication.service;

import com.bequitebtw.socialnetwork.common.exception.BadCredentialsAuthenticationException;
import com.bequitebtw.socialnetwork.common.util.JwtUtil;
import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	@Override
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.password()));
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateJwt(userDetails);
			return new AuthenticationResponse(jwt);
		} catch (BadCredentialsException ex) {
			throw new BadCredentialsAuthenticationException();
		}
	}
}
