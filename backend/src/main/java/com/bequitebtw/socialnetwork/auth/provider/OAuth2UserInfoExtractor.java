package com.bequitebtw.socialnetwork.auth.provider;

import java.util.Map;

public interface OAuth2UserInfoExtractor {
    OAuth2UserInfo extract(Map<String, Object> attributes);
}
