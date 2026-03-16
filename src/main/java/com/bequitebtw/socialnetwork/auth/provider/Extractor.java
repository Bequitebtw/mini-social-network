package com.bequitebtw.socialnetwork.auth.provider;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class Extractor {
    public static String getField(Map<String, Object> attributes,String key) {
        Object value = attributes.get(key);
        if (value == null) {
            throw new OAuth2AuthenticationException("Missing required attribute: " + key);
        }
        return value.toString();
    }
    public static String getFieldOrDefault(Map<String, Object> attributes, String key, String defaultValue) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
