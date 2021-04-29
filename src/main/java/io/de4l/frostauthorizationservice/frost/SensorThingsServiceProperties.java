package io.de4l.frostauthorizationservice.frost;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SensorThingsServiceProperties {

    @Value("${app.sta.url}")
    private String frostUri;

    @Value("${app.sta.oidc.url}")
    private String oidcUri;

    @Value("${app.sta.oidc.username}")
    private String oidcUsername;

    @Value("${app.sta.oidc.password}")
    private String oidcPassword;

    @Value("${app.sta.oidc.clientId}")
    private String oidcClientId;
}
