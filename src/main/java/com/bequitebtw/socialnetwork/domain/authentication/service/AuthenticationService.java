package com.bequitebtw.socialnetwork.domain.authentication.service;


import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationRequest;
import com.bequitebtw.socialnetwork.domain.authentication.dto.AuthenticationResponse;

public interface AuthenticationService {
	AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
