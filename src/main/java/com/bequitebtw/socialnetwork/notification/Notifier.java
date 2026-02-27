package com.bequitebtw.socialnetwork.notification;

public interface Notifier {
	void sendVerificationToken(String recipient, String token);
}
