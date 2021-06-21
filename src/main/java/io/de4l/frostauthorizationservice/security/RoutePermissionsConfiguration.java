package io.de4l.frostauthorizationservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RoutePermissionsConfiguration {

    @Value("${security.admin.role}")
    private String ROLE_ADMIN;


    public HttpSecurity configureHttpSecurityPermissions(HttpSecurity httpSecurity) throws Exception {
        Assert.notNull(this.ROLE_ADMIN, "ADMIN role can not be null, set 'security.admin.role' in application properties.");
        return httpSecurity
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/FROST-Server/v1.0/Things").hasRole(ROLE_ADMIN)
                .and();

    }
}
