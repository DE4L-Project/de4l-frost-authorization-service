package io.de4l.frostauthorizationservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RoutePermissionsConfiguration {

    @Value("${security.admin.role}")
    private String ADMIN_ROLE;

    public HttpSecurity configureHttpSecurityPermissions(HttpSecurity httpSecurity) throws Exception {
        Assert.notNull(this.ADMIN_ROLE, "ADMIN role can not be null, set 'security.admin.role' in application properties.");
        return httpSecurity.authorizeRequests().anyRequest().fullyAuthenticated().and();
    }
}
