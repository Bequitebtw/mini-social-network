package com.bequitebtw.socialnetwork.config.handler;

import com.bequitebtw.socialnetwork.auth.cookie.RefreshTokenCookieFactory;
import com.bequitebtw.socialnetwork.auth.dto.RefreshTokenResponse;
import com.bequitebtw.socialnetwork.auth.model.RefreshToken;
import com.bequitebtw.socialnetwork.auth.provider.OAuth2UserInfo;
import com.bequitebtw.socialnetwork.auth.provider.OAuth2UserInfoExtractor;
import com.bequitebtw.socialnetwork.auth.service.RefreshTokenService;
import com.bequitebtw.socialnetwork.common.util.JwtUtil;
import com.bequitebtw.socialnetwork.security.UserPrincipal;
import com.bequitebtw.socialnetwork.auth.model.AuthProvider;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.auth.model.UserAuthProvider;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;
    private final Map<String, OAuth2UserInfoExtractor> providers;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();
        OAuth2UserInfo userInfo = providers.get(registrationId.toLowerCase()).extract(oAuth2User.getAttributes());

        User user = userRepository.findUserByEmailIgnoreCase(userInfo.email())
                .map(foundUser -> linkProvider(foundUser, registrationId, userInfo.providerUserId()))
                .orElseGet(() -> createUserWithUniqueUsername(userInfo.email(), userInfo.name()));

        if (user.getProviders().stream()
                .noneMatch(p -> p.getProvider().name().equalsIgnoreCase(registrationId))) {
            UserAuthProvider userAuthProvider = new UserAuthProvider();
            userAuthProvider.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            userAuthProvider.setProviderUserId(userInfo.providerUserId());
            userAuthProvider.setUser(user);
            user.getProviders().add(userAuthProvider);
            user = userRepository.save(user);
        }

        if (refreshTokenService.existsByUser(user)) {
            refreshTokenService.deleteByUser(user);
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);
        String accessToken = jwtUtil.generateAccessJwt(userPrincipal);

        String refreshToken = jwtUtil.generateRefreshJwt();
        while (refreshTokenService.existByToken(refreshToken)) {
            refreshToken = jwtUtil.generateRefreshJwt();
        }


        refreshTokenService.save(new RefreshToken(user, Instant.now().plus(Duration.ofDays(30)), refreshToken));

        SecurityContextHolder.clearContext();

        response.addHeader("Set-Cookie", refreshTokenCookieFactory.create(
                new RefreshTokenResponse(refreshToken, Instant.now().plus(Duration.ofDays(30)))).toString());

        String redirectUrl = UriComponentsBuilder.fromPath("/oauth/callback")
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);

        log.info("OAuth2 Account with email {} and username {} processed successfully", user.getEmail(), user.getUsername());
    }


    private User createUserWithUniqueUsername(String email, String name) {
        User user = new User();
        user.setEmail(email);

        String baseName = name.replaceAll("\\s+", "_");
        String newName = baseName;
        while (userRepository.existsByUsernameIgnoreCase(newName)) {
            newName = baseName + "_" + UUID.randomUUID().toString().substring(0, 8);
        }
        user.setUsername(newName);

        return userRepository.save(user);
    }

    private User linkProvider(User user, String registrationId, String providerUserId) {
        boolean hasProvider = user.getProviders().stream()
                .anyMatch(p -> p.getProvider().name().equalsIgnoreCase(registrationId));

        if (!hasProvider) {
            UserAuthProvider userAuthProvider = new UserAuthProvider();
            userAuthProvider.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            userAuthProvider.setProviderUserId(providerUserId);
            userAuthProvider.setUser(user);
            user.getProviders().add(userAuthProvider);
            return userRepository.save(user);
        }
        return user;
    }
}
