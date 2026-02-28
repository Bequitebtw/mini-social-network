package com.bequitebtw.socialnetwork.domain.registration.repository;

import com.bequitebtw.socialnetwork.domain.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
	Optional<Registration> findByToken(String token);

	Optional<Registration> findByEmail(String email);
}
