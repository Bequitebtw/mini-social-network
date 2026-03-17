package com.bequitebtw.socialnetwork.security;


import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class SecureRandomTokenGenerator implements TokenGenerator {
	@Override
	public String generate() {
		byte[] tokenBytes = KeyGenerators.secureRandom(32).generateKey();
		return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
	}
}
