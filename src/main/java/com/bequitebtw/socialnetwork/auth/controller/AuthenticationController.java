package com.bequitebtw.socialnetwork.auth.controller;


import com.bequitebtw.socialnetwork.auth.cookie.RefreshTokenCookieFactory;
import com.bequitebtw.socialnetwork.auth.dto.AccessTokenResponse;
import com.bequitebtw.socialnetwork.auth.dto.RefreshTokenResponse;
import com.bequitebtw.socialnetwork.common.ApiResponse;
import com.bequitebtw.socialnetwork.common.builder.ResponseBuilder;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationResponse;
import com.bequitebtw.socialnetwork.auth.service.AuthenticationService;
import com.bequitebtw.socialnetwork.common.util.JwtUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> login(@Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest request) throws Exception {
        log.info("Login request from: {}", authenticationRequest.login());
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
        RefreshTokenResponse refreshToken = authenticationResponse.refreshTokenResponse();
        ResponseCookie responseCookie = refreshTokenCookieFactory.create(refreshToken);

        return ResponseBuilder.ok()
                .cookie(responseCookie)
                .data(authenticationResponse.accessTokenResponse())
                .instance(request.getRequestURI())
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal JwtUserPrincipal jwtUserPrincipal) {
        log.info("Logout request from: {}", jwtUserPrincipal.getUsername());
        ResponseCookie responseCookie = refreshTokenCookieFactory.delete();
        authenticationService.logout(jwtUserPrincipal);
        return ResponseBuilder.ok().cookie(responseCookie).build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(@CookieValue("refresh_token") String refreshToken, HttpServletRequest request) {
        log.info("Refresh token request");
        AuthenticationResponse authenticationResponse = authenticationService.refreshToken(refreshToken);
        RefreshTokenResponse token = authenticationResponse.refreshTokenResponse();
        ResponseCookie responseCookie = refreshTokenCookieFactory.create(token);
        return ResponseBuilder.ok()
                .cookie(responseCookie)
                .data(authenticationResponse.accessTokenResponse())
                .instance(request.getRequestURI())
                .build();
    }
}