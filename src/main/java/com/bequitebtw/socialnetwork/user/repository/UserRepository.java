package com.bequitebtw.socialnetwork.user.repository;

import com.bequitebtw.socialnetwork.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByEmailIgnoreCase(String Email);
	boolean existsByUsernameIgnoreCase(String username);

	Optional<User> findUserByUsernameIgnoreCase(String username);

	Optional<User> findUserByUsernameIgnoreCaseOrEmailIgnoreCase(String username,String email);
}
