package com.bequitebtw.socialnetwork.auth.service;

import com.bequitebtw.socialnetwork.auth.dto.AccessTokenResponse;
import com.bequitebtw.socialnetwork.auth.mapper.RefreshTokenMapper;
import com.bequitebtw.socialnetwork.common.exception.ExpiredRefreshTokenException;
import com.bequitebtw.socialnetwork.common.exception.UserNotFoundException;
import com.bequitebtw.socialnetwork.common.util.JwtUserPrincipal;
import com.bequitebtw.socialnetwork.common.util.JwtUtil;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationResponse;
import com.bequitebtw.socialnetwork.auth.model.RefreshToken;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.security.UserPrincipal;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Value("${jwt.refresh-lifetime}")
    private Duration refreshTime;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.password()));
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new UserNotFoundException(userPrincipal.getId()));

            if (refreshTokenService.existsByUser(user)) {
                refreshTokenService.deleteByUser(user);
            }

            String accessJwt = jwtUtil.generateAccessJwt(userPrincipal);
            String refreshJwt = jwtUtil.generateRefreshJwt();

            while (refreshTokenService.existByToken(refreshJwt)) {
                refreshJwt = jwtUtil.generateRefreshJwt();
            }
            RefreshToken refreshToken = new RefreshToken(user, Instant.now().plus(refreshTime), refreshJwt);
            refreshTokenService.save(refreshToken);
            return new AuthenticationResponse(new AccessTokenResponse(accessJwt), refreshTokenMapper.toResponse(refreshToken));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Неверный пароль");
        }
    }

    @Override
    public void logout(JwtUserPrincipal jwtUserPrincipal) {
        User user = userRepository.findById(jwtUserPrincipal.getId()).orElseThrow(() -> new UserNotFoundException(jwtUserPrincipal.getId()));
        refreshTokenService.deleteByUser(user);
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken);
        if (token.isExpired()) {
            log.warn("Refresh token was expired {}", refreshToken);
            refreshTokenService.deleteByToken(token.getToken());
            throw new ExpiredRefreshTokenException();
        }
        String newToken = jwtUtil.generateRefreshJwt();
        while (refreshTokenService.existByToken(newToken)) {
            newToken = jwtUtil.generateRefreshJwt();
        }
        String accessJwt = jwtUtil.generateAccessJwt(new UserPrincipal(token.getUser()));
        token.setToken(newToken);
        token.setExpiryDate(Instant.now().plus(refreshTime));
        refreshTokenService.save(token);
        log.warn("New jwt and refresh tokens created success");
        return new AuthenticationResponse(new AccessTokenResponse(accessJwt), refreshTokenMapper.toResponse(token));
    }
}
