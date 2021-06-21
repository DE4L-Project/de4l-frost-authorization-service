package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.StaEntity;
import io.de4l.frostauthorizationservice.model.Thing;
import io.de4l.frostauthorizationservice.security.KeycloakUser;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
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
    private final ResponseEntity UNAUTHORIZED = new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    private final ResponseEntity NOTHING_FOUND = new ResponseEntity<>("Nothing found", HttpStatus.NOT_FOUND);

    private static final String FILTER = "$filter";

    @Autowired
    private RestTemplate restTemplate;

    protected BaseRestController(SensorThingsServiceProperties sensorThingsServiceProperties, StaEntity staEntity) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
        this.staEntity = staEntity;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleRestClientException(HttpClientErrorException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getResponseBodyAsString(), getErrorHttpHeaders(), e.getStatusCode());
    }

    protected ResponseEntity<String> performReadRequest(HttpServletRequest request, JwtAuthenticationToken token, String expand) throws RestClientException {
        URI requestUri;
        if (token == null) {
            // Public Request
            requestUri = this.buildPublicRequestUrl(request.getRequestURI(), expand);
        } else {
            // Potential authenticated Request
            var keycloakUser = new KeycloakUser(token);
            if (keycloakUser.isAdmin()) {
                // Admin request
                requestUri = this.buildUnfilteredUri(request.getRequestURI(), expand);
            } else {
                // Consumer/Owner request
                requestUri = this.buildPrivateRequestUri(request.getRequestURI(), keycloakUser.getUserId(), expand);
            }
        }
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity<String>(null, buildRequestHeaders()), String.class);
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (HttpStatusCodeException e) {
            return NOTHING_FOUND;
        }
    }

    // TODO: Try catch 'Not Found' error from Frost ?
    protected  ResponseEntity<String> performCreateRequest(HttpServletRequest request, JwtAuthenticationToken token, String body) {
        var keycloakUser = new KeycloakUser(token);
        if (!keycloakUser.isAdmin()) {
            if (staEntity.getClass().equals(Thing.class)) {
                return UNAUTHORIZED;
            } else {
                if (!isPrincipalTheThingOwner(request.getRequestURI(), keycloakUser.getUserId())) {
                    return NOTHING_FOUND;
                }
            }
        }
        ResponseEntity<String> response = restTemplate.exchange(
                buildUnfilteredUri(request.getRequestURI(), ""),
                HttpMethod.POST,
                new HttpEntity<>(body, buildRequestHeaders()), String.class);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    protected ResponseEntity<String> performUpdateRequest(HttpServletRequest request, JwtAuthenticationToken token, String body) throws RestClientException {
        if (token == null) {
            return UNAUTHORIZED;
        }
        // Check whether requested resource references to the Keycloak id as it's thing owner
        var keycloakUser = new KeycloakUser(token);
        if (keycloakUser.isAdmin()
                || isPrincipalTheThingOwner(request.getRequestURI(), keycloakUser.getUserId())) {
            var requestUri = buildUnfilteredUri(request.getRequestURI(), "");
            var response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.valueOf(request.getMethod()),
                    new HttpEntity<>(body, buildRequestHeaders()), String.class);
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } else {
            return NOTHING_FOUND;
        }
    }

    // TODO: Validate check!
    protected boolean isPrincipalTheThingOwner(String requestUriString, String principalId) {
        try {
            var requestUri = buildOwnerRequestUri(requestUriString, principalId);
            System.out.println(requestUri);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity<>(null, buildRequestHeaders()), String.class);
            return (response.getBody().contains("@"));
        } catch (HttpStatusCodeException e) {
            return false;
        }
    }

    protected HttpHeaders buildRequestHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected URI buildUnfilteredUri(String requestUri, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam(FILTER, expand);
        }
        return uriComponentsBuilder.build().toUri();

    }

    protected URI buildOwnerRequestUri(String requestUri, String userId) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildOwnerFilter(userId));
        return uriComponentsBuilder.build().toUri();
    }

    protected URI buildPrivateRequestUri(String requestUri, String userId, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildOwnerFilter(userId) + " or " + buildPublicFilter() + " or " + buildConsumerFilter(userId));

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam(FILTER, expand);
        }

        return uriComponentsBuilder.build().toUri();
    }

    protected URI buildPublicRequestUrl(String requestUri, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildPublicFilter());

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
