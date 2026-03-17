package com.bequitebtw.socialnetwork.auth.provider;

import org.springframework.stereotype.Component;

import java.util.Map;


@Component("discord")
public class OAuth2DiscordProvider implements OAuth2UserInfoExtractor {
    @Override
    public OAuth2UserInfo extract(Map<String, Object> attributes) {
        String id = Extractor.getField(attributes, "id");

        String username = Extractor.getField(attributes, "username");
//        String globalName = Extractor.getField(attributes, "global_name");
//        String discriminator = Extractor.getField(attributes, "discriminator");

        String email = (String) attributes.get("email");
        if (email == null) {
            email = id + "@discord.user";
        }

        return new OAuth2UserInfo(id, email, username, null);
    }
}
