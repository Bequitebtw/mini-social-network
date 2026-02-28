package com.bequitebtw.socialnetwork.security;

import com.bequitebtw.socialnetwork.domain.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;


public class UserPrincipal implements UserDetails {
	@Getter
	private final UUID id;
	private final String username;
	private final String password;
	private final String role;
	private final boolean banned;

	public UserPrincipal(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.role = user.getRole().name();
		this.banned = user.isBanned();
	}

	@Override
	@NonNull
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(this.role));
	}

	@Override
	public @Nullable String getPassword() {
		return this.password;
	}

	@Override
	@NonNull
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.banned;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
