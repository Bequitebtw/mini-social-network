package com.bequitebtw.socialnetwork.common;

import com.bequitebtw.socialnetwork.user.model.Role;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Transactional
public class DataLoader implements CommandLineRunner {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String @NonNull ... args) throws Exception {
		User user1 = User.builder()
				.username("4444")
				.password(passwordEncoder.encode("Qeadws1234"))
				.email("Bequitebtw@mail.ru")
				.createdAt(LocalDateTime.now())
				.role(Role.USER).isBanned(false).build();
		User user2 = User.builder()
				.username("1111")
				.password(passwordEncoder.encode("Qeadws1234"))
				.email("gavrilovdts@gmail.com")
				.createdAt(LocalDateTime.now())
				.role(Role.USER).isBanned(false).build();

		System.out.printf("user1: %s\n", user1);
		System.out.printf("user2: %s\n", user2);
//		userRepository.save(user1);
//		userRepository.save(user2);
	}
}
