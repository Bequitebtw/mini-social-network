package com.bequitebtw.socialnetwork.registration.dto;


import java.util.UUID;

public record RegistrationResponse(UUID id, String username, String email) {

}
