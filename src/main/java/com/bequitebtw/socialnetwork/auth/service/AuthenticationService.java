package com.bequitebtw.socialnetwork.auth.service;


import com.bequitebtw.socialnetwork.auth.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.auth.dto.AuthenticationResponse;
import com.bequitebtw.socialnetwork.common.util.JwtUserPrincipal;

public interface AuthenticationService {
	AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

	void logout(JwtUserPrincipal jwtUserPrincipal);
}
