package com.bequitebtw.socialnetwork.auth.repository;

import com.bequitebtw.socialnetwork.auth.model.RefreshToken;
import com.bequitebtw.socialnetwork.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByToken(String token);

    boolean existsByToken(String token);

    void deleteByExpiryDateBefore(Instant instant);

    boolean existsByUser(User user);
}
