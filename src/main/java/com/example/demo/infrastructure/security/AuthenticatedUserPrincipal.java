package com.example.demo.infrastructure.security;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record AuthenticatedUserPrincipal(
        String userId,
        String email,
        String role) implements Principal {

    @Override
    public String getName() {
        return email;
    }

    public Collection<? extends GrantedAuthority> authorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
