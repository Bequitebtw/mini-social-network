package com.bequitebtw.socialnetwork.user.dto;


import java.time.LocalDateTime;

public record UserShort(String id, String username, String email, LocalDateTime createdAt) {
}
