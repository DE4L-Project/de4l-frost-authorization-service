package io.de4l.frostauthorizationservice.controller;

import de.fraunhofer.iosb.ilt.sta.StatusCodeException;
import io.de4l.frostauthorizationservice.controller.sta.StaEntity;
import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.security.KeycloakPrincipal;
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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;

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

    protected String executeFrostRequest(JwtAuthenticationToken token, String expand) throws RestClientException {
        return executeFrostRequest(null, token, expand);
    }

    protected String executeFrostRequest(String id, JwtAuthenticationToken token, String expand) throws RestClientException {
        KeycloakPrincipal keycloakPrincipal = new KeycloakPrincipal(token);
        RestTemplate restTemplate = new RestTemplate();

        URI requestUri = this.buildFrostRequestUrl(id, keycloakPrincipal.getUserId(), expand);
        ResponseEntity<String> response = restTemplate.exchange(
                requestUri,
                HttpMethod.GET,
                new HttpEntity(null, buildRequestHeaders(keycloakPrincipal.getJwtAuthenticationToken().getToken().getTokenValue())),
                String.class);
        return response.getBody();
    }

    protected HttpHeaders buildRequestHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected URI buildFrostRequestUrl(String userId, String expand) {
        return this.buildFrostRequestUrl(null, userId, expand);
    }

    protected URI buildFrostRequestUrl(String id, String userId, String expand) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + staEntity.getUrlPath(id))
                .queryParam("$filter",
                        staEntity.getThingPropertyPath() + "/" + sensorThingsServiceProperties.getOwnerIdProperty() + " eq '" + userId +
                                "' or substringOf('\"" + userId + "\"'," + staEntity.getThingPropertyPath() + "/" + sensorThingsServiceProperties.getSharedWithIdsProperty() + ")");

        if (Strings.isNotBlank(expand)) {
            builder.queryParam("$expand", expand);
        }

        return builder.build().toUri();
    }

    @ExceptionHandler(StatusCodeException.class)
    public ResponseEntity<Object> handleStaServiceException(StatusCodeException e) {
        log.error(e.getReturnedContent(), e);
        return new ResponseEntity<>(e.getReturnedContent(), getErrorHttpHeaders(), e.getStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleRestClientException(HttpClientErrorException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getResponseBodyAsString(), getErrorHttpHeaders(), e.getStatusCode());
    }

    private HttpHeaders getErrorHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
