package io.de4l.frostauthorizationservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SensorThingsServiceProperties {

    @Value("${app.sta.url}")
    private String frostUri;

    @Value("${app.sta.ownerProperty}")
    private String ownerProperty;

    @Value("${app.sta.publicProperty}")
    private String publicProperty;

    @Value("de4lConsumer")
    private String consumerProperty;

    private final String FILTER = "$filter";
}
