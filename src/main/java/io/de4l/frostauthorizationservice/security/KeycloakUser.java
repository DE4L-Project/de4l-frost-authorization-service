package io.de4l.frostauthorizationservice.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class KeycloakUser implements Principal {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String USER_ID_KEY = "sub";
    private final static String USER_PREFERRED_USERNAME_KEY = "preferred_username";
    private List<String> list;

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

    public boolean isAdmin() throws IOException {
        var clients = jwtAuthenticationToken.getTokenAttributes().get("resource_access").toString();
        var jsonNode = objectMapper.readTree(clients);
        var arrayNode = (ArrayNode) jsonNode.at("/de4l-frost-authorization-service/roles");
        ObjectReader reader = objectMapper.readerFor(new TypeReference<List<String>>() {
        });
        List<String> list = reader.readValue(arrayNode);
        return list.contains("admin");

    }
}
