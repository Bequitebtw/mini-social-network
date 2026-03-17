package com.bequitebtw.socialnetwork.security;

import com.bequitebtw.socialnetwork.common.exception.LoginNotFoundException;
import com.bequitebtw.socialnetwork.auth.model.AuthProvider;
import com.bequitebtw.socialnetwork.user.model.User;
import com.bequitebtw.socialnetwork.auth.model.UserAuthProvider;
import com.bequitebtw.socialnetwork.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @NonNull
    @Transactional
    public UserDetails loadUserByUsername(@NonNull String login) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsernameIgnoreCaseOrEmailIgnoreCase(login, login).orElseThrow(() -> new LoginNotFoundException(login));
        UserAuthProvider localProvider = user.getProviders().stream()
                .filter(p -> p.getProvider() == AuthProvider.LOCAL)
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Local auth not configured for user: " + login));

        return new UserPrincipal(user, localProvider.getPassword());
    }
}
