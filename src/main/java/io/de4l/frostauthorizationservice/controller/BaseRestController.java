package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.StaEntity;
import io.de4l.frostauthorizationservice.security.KeycloakUser;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RequestMapping(path = "/FROST-Server/v1.0/")
@CrossOrigin(origins = "*")
@Log4j2
public abstract class BaseRestController {
    protected final SensorThingsServiceProperties sensorThingsServiceProperties;
    protected final StaEntity staEntity;

    public BaseRestController(SensorThingsServiceProperties sensorThingsServiceProperties, StaEntity staEntity) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
        this.staEntity = staEntity;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleRestClientException(HttpClientErrorException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getResponseBodyAsString(), getErrorHttpHeaders(), e.getStatusCode());
    }

    protected String executeFrostRequest(HttpServletRequest request, JwtAuthenticationToken token, String expand) throws RestClientException {
        KeycloakUser keycloakUser = new KeycloakUser(token);
        RestTemplate restTemplate = new RestTemplate();

        URI requestUri = this.buildFrostRequestUrl(request, keycloakUser.getUserId(), expand);
        ResponseEntity<String> response = restTemplate.exchange(
                requestUri,
                HttpMethod.GET,
                new HttpEntity<String>(null, buildRequestHeaders(keycloakUser.getJwtAuthenticationToken().getToken().getTokenValue())), String.class);
        return response.getBody();
    }

    protected HttpHeaders buildRequestHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected URI buildFrostRequestUrl(HttpServletRequest request, String userId, String expand) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + request.getRequestURI());
        uriComponentsBuilder.queryParam("$filter", buildOwnerQuery(userId) + " or " + buildSharedWithQuery(userId));

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam("$expand", expand);
        }

        return uriComponentsBuilder.build().toUri();
    }

    private HttpHeaders getErrorHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    private String buildOwnerQuery(String userId) {
        return String.format("%s/%s eq '%s'", staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getOwnerIdProperty(), userId);
    }

    private String buildSharedWithQuery(String userId) {
        return String.format("substringOf('\"%s\"',%s/%s)", userId, staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getSharedWithIdsProperty());
    }
}
