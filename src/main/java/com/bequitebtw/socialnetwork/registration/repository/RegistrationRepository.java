package com.bequitebtw.socialnetwork.registration.repository;

import com.bequitebtw.socialnetwork.registration.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
	Optional<Registration> findByToken(String token);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByToken(String token);

	Optional<Registration> findByEmailIgnoreCase(String email);
}
