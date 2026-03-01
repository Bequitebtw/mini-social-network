package com.bequitebtw.socialnetwork.auth.dto;

public record AuthenticationResponse(String jwtToken, String refreshToken) {

}
