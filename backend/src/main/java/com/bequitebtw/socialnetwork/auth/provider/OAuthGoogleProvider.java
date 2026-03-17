package com.bequitebtw.socialnetwork.auth.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component("google")
@RequiredArgsConstructor
public class OAuthGoogleProvider implements OAuth2UserInfoExtractor {

    @Override
    public OAuth2UserInfo extract(Map<String, Object> attributes) {
        String providerUserId = Extractor.getField(attributes, "sub");
        String email = Extractor.getField(attributes, "email");
        String name = Extractor.getFieldOrDefault(attributes, "name", "user");
        String picture = Extractor.getFieldOrDefault(attributes, "picture", null);
        return new OAuth2UserInfo(providerUserId, email, name, picture);
    }
}
