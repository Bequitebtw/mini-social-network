package com.bequitebtw.socialnetwork.auth.provider;

public record OAuth2UserInfo(
        String providerUserId,
        String email,
        String name,
        String picture
) {}