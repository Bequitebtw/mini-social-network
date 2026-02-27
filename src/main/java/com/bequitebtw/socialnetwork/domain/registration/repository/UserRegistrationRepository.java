package com.bequitebtw.socialnetwork.domain.registration.repository;

import com.bequitebtw.socialnetwork.domain.registration.entity.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRegistrationRepository extends JpaRepository<UserRegistration, UUID> {
	Optional<UserRegistration> findByToken(String token);

	Optional<UserRegistration> findByEmail(String email);
}
