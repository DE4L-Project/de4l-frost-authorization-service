package io.de4l.frostauthorizationservice.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class KeycloakUser implements Principal {
    private final static String USER_ID_KEY = "sub";
    private final static String USER_PREFERRED_USERNAME_KEY = "preferred_username";

    @JsonIgnore
    private final JwtAuthenticationToken jwtAuthenticationToken;

    public KeycloakUser(JwtAuthenticationToken jwtAuthenticationToken) {
        Assert.notNull(jwtAuthenticationToken, "jwtAuthenticationToken can not be null!");
        this.jwtAuthenticationToken = jwtAuthenticationToken;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

    public String getUserId() {
        return (String) this.jwtAuthenticationToken.getTokenAttributes().get(USER_ID_KEY);
    }

    public String getUsername() {
        return (String) this.jwtAuthenticationToken.getTokenAttributes().get(USER_PREFERRED_USERNAME_KEY);
    }

    public List<String> getRealmRoles() {
        return jwtAuthenticationToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }


}
