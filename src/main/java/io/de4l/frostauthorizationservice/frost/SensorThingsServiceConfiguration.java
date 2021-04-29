package io.de4l.frostauthorizationservice.frost;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.ilt.sta.service.TokenManagerOpenIDConnect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class SensorThingsServiceConfiguration {

    private final SensorThingsServiceProperties sensorThingsServiceProperties;

    public SensorThingsServiceConfiguration(SensorThingsServiceProperties sensorThingsServiceProperties) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
    }

    @Bean
    SensorThingsService sensorThingsService() throws URISyntaxException, MalformedURLException {
        SensorThingsService sensorThingsService = new SensorThingsService(new URI(sensorThingsServiceProperties.getFrostUri()));
        TokenManagerOpenIDConnect openIDConnect = new TokenManagerOpenIDConnect();
        openIDConnect.setClientId(sensorThingsServiceProperties.getOidcClientId());
        openIDConnect.setUserName(sensorThingsServiceProperties.getOidcUsername());
        openIDConnect.setPassword(sensorThingsServiceProperties.getOidcPassword());
        openIDConnect.setTokenServerUrl(sensorThingsServiceProperties.getOidcUri());

        sensorThingsService.setTokenManager(openIDConnect);
        return sensorThingsService;
    }
}
