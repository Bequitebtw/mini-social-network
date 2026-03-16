package com.bequitebtw.socialnetwork.auth.provider;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component("github")
public class OAuthGitHubProvider implements OAuth2UserInfoExtractor {

    @Override
    public OAuth2UserInfo extract(Map<String, Object> attributes) {
        String id = Extractor.getField(attributes, "id");
        String login = Extractor.getField(attributes, "login");
        String email = Extractor.getFieldOrDefault(attributes, "email", id + "@yourtemporaryemail.com");
        String picture = Extractor.getFieldOrDefault(attributes, "picture", null);
        return new OAuth2UserInfo(id, email, login, picture);
    }
}
