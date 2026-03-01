package com.bequitebtw.socialnetwork.user.repository;

import com.bequitebtw.socialnetwork.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByEmail(String Email);
	boolean existsByUsername(String username);

	Optional<User> findUserByUsername(String username);

	Optional<User> findUserByUsernameOrEmail(String username,String email);
}
