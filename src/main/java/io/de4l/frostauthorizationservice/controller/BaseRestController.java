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

    protected BaseRestController(SensorThingsServiceProperties sensorThingsServiceProperties, StaEntity staEntity) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
        this.staEntity = staEntity;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleRestClientException(HttpClientErrorException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getResponseBodyAsString(), getErrorHttpHeaders(), e.getStatusCode());
    }

    protected String performReadRequest(HttpServletRequest request, JwtAuthenticationToken token, String expand) throws RestClientException {
        URI requestUri;
        if (token == null) {
            // Public Request
            requestUri = this.buildUnauthorizedRequestUrl(request, expand);
        } else {
            // Potential authenticated Request
            var keycloakUser = new KeycloakUser(token);
            requestUri = this.buildAuthorizedRequestUri(request, keycloakUser.getUserId(), expand);
        }

        var restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                requestUri,
                HttpMethod.GET,
                new HttpEntity<String>(null, buildRequestHeaders()), String.class);
        return response.getBody();
    }


    protected HttpHeaders buildRequestHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected URI buildAuthorizedRequestUri(HttpServletRequest request, String userId, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + request.getRequestURI());
        uriComponentsBuilder.queryParam("$filter", buildOwnerFilter(userId) + " or " + buildPublicFilter() + " or " + buildConsumerFilter(userId));

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam("$expand", expand);
        }

        return uriComponentsBuilder.build().toUri();
    }

    protected URI buildUnauthorizedRequestUrl(HttpServletRequest request, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + request.getRequestURI());
        uriComponentsBuilder.queryParam("$filter",buildPublicFilter());

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam("$expand", expand);
        }

        return uriComponentsBuilder.build().toUri();
    }

    private HttpHeaders getErrorHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    private String buildOwnerFilter(String userId) {
        return String.format("%s/%s eq '%s'", staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getOwnerProperty(), userId);
    }

    private String buildPublicFilter() {
        return String.format("%s/%s eq 'true'", staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getPublicProperty());
    }

    private String buildConsumerFilter(String userId) {
        return String.format("substringOf('\"%s\"',%s/%s)", userId, staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getConsumerProperty());
    }


}
