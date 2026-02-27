package com.bequitebtw.socialnetwork.domain.registration.dto;


import java.util.UUID;

public record UserRegistrationResponse(UUID id, String username, String email,String token) {

}
