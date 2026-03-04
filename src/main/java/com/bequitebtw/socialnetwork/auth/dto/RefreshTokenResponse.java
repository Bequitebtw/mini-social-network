package com.bequitebtw.socialnetwork.auth.dto;


import java.time.Instant;

public record RefreshTokenResponse(String token, Instant expiryDate) {
}
