package com.bequitebtw.socialnetwork.domain.registration.dto;


import java.util.UUID;

public record RegistrationResponse(UUID id, String username, String email, String token) {

}
