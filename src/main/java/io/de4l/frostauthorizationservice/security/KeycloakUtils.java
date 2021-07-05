package io.de4l.frostauthorizationservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakUtils {

    @Value("${app.sta.keycloakAdminRole}")
    private String ROLE_ADMIN;

    public boolean isNotAuthenticated() {
        return !SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    public List<String> getRealmRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public boolean isAdmin() {
        return getRealmRoles().contains(ROLE_ADMIN);
    }

    public String getName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
