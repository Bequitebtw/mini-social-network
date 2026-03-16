package com.bequitebtw.socialnetwork.auth.model;

import com.bequitebtw.socialnetwork.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_auth_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthProvider {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "provider_user_id")
    private String providerUserId;

    @Column(name = "password", nullable = true)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
