package io.de4l.frostauthorizationservice.controller;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequestMapping(path = "/FROST-Server/v1.0/")
public class BaseRestController {
    protected final SensorThingsServiceProperties sensorThingsServiceProperties;

    public BaseRestController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
    }

    protected HttpHeaders buildRequestHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected URI buildFrostRequestUrl(String path, String username, String expand) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + path)
                .queryParam("$filter", "properties/" + sensorThingsServiceProperties.getOwnerIdProperty() + " eq '" + username + "' or substringOf('\"" + username + "\"',properties/" + sensorThingsServiceProperties.getSharedWithIdsProperty() + ")");

        if (Strings.isNotBlank(expand)) {
            builder.queryParam("$expand", expand);
        }

        return builder.build().toUri();
    }

}
