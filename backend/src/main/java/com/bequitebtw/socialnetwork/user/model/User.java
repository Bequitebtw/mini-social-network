package com.bequitebtw.socialnetwork.user.model;

import com.bequitebtw.socialnetwork.auth.model.UserAuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    @Column(name = "username", nullable = false, length = 20, unique = true)
    private String username;
    @Column(name = "email", unique = true, nullable = false, length = 50)
    private String email;
    @Column(name = "isBanned")
    boolean isBanned = false;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<UserAuthProvider> providers = new ArrayList<>();
}
