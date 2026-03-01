package com.bequitebtw.socialnetwork.auth.service;

import com.bequitebtw.socialnetwork.common.exception.BadCredentialsAuthenticationException;
import com.bequitebtw.socialnetwork.common.exception.UserNotFoundException;
import com.bequitebtw.socialnetwork.common.util.JwtUserPrincipal;
import com.bequitebtw.socialnetwork.common.util.JwtUtil;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationResponse;
import com.bequitebtw.socialnetwork.auth.model.RefreshToken;
import com.bequitebtw.socialnetwork.security.TokenGenerator;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.security.UserPrincipal;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;
	private final TokenGenerator tokenGenerator;
	private final UserRepository userRepository;

	@Value("${jwt.refresh-lifetime}")
	private Duration refreshTime;

	@Override
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.password()));
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new UserNotFoundException(userPrincipal.getId()));
			String accessJwt = jwtUtil.generateAccessJwt(userPrincipal);
			String refreshJwt = tokenGenerator.generate();
			while (refreshTokenService.existByToken(refreshJwt)) {
				refreshJwt = tokenGenerator.generate();
			}
			RefreshToken refreshToken = new RefreshToken(user, Instant.now().plus(refreshTime), refreshJwt);
			refreshTokenService.save(refreshToken);
			return new AuthenticationResponse(accessJwt, refreshJwt);
		} catch (BadCredentialsException ex) {
			throw new BadCredentialsAuthenticationException();
		}
	}

	@Override
	public void logout(JwtUserPrincipal jwtUserPrincipal) {
		User user = userRepository.findById(jwtUserPrincipal.getId()).orElseThrow(() -> new UserNotFoundException(jwtUserPrincipal.getId()));
		refreshTokenService.deleteByUser(user);
	}
}
