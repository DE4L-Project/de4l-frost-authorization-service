package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.StaEntity;
import io.de4l.frostauthorizationservice.model.Thing;
import io.de4l.frostauthorizationservice.security.KeycloakUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
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
import java.util.List;

@RequestMapping(path = "/FROST-Server/v1.0/")
@CrossOrigin(origins = "*")
@Log4j2
public abstract class BaseRestController {
    protected final SensorThingsServiceProperties sensorThingsServiceProperties;
    protected final StaEntity staEntity;
    private final ResponseEntity<String> UNAUTHORIZED = new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    private final ResponseEntity<String> NOTHING_FOUND = new ResponseEntity<>("Nothing found", HttpStatus.NOT_FOUND);
    private final ObjectMapper objectMapper;
    protected final KeycloakUtils keycloakUtils;
    private static final String FILTER = "$filter";

    @Autowired
    private RestTemplate restTemplate;

    protected BaseRestController(SensorThingsServiceProperties sensorThingsServiceProperties, StaEntity staEntity, ObjectMapper objectMapper, KeycloakUtils keycloakUtils) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
        this.staEntity = staEntity;
        this.objectMapper = objectMapper;
        this.keycloakUtils = keycloakUtils;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleRestClientException(HttpClientErrorException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getResponseBodyAsString(), getErrorHttpHeaders(), e.getStatusCode());
    }

    public ResponseEntity<String> performReadRequest(HttpServletRequest request, String expand) throws RestClientException {
        URI requestUri;
        if (!keycloakUtils.isNotAuthenticated()) {
            // Public Request
            requestUri = this.buildPublicRequestUrl(request.getRequestURI(), expand);
        } else {
            // Potential authenticated Request
            if (keycloakUtils.isAdmin()) {
                // Admin request
                requestUri = this.buildUnfilteredUri(request.getRequestURI(), expand);
            } else {
                // Consumer/Owner request
                requestUri = this.buildPrivateRequestUri(request.getRequestURI(), keycloakUtils.getName(), expand);
            }
        }
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity<String>(null, buildRequestHeaders()), String.class);
            var cleanedResponse = removeFilterFromResponse(response.getBody());
            return new ResponseEntity<>(cleanedResponse, response.getStatusCode());
        } catch (HttpStatusCodeException | JsonProcessingException e) {
            return NOTHING_FOUND;
        }
    }

    public String removeFilterFromResponse(String response) throws JsonProcessingException {
        final var NEXT_LINK = "@iot.nextLink";
        var jsonNode = objectMapper.readTree(response);
        var nextLink = jsonNode.at("/" + NEXT_LINK).asText();
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(nextLink).build().getQueryParams();
        List<String> filterParameters = parameters.get(FILTER);
        if (filterParameters == null) {
            return response;
        }
        var nextLinkCleaned = nextLink
                .replace("&"+FILTER+"=", "")
                .replace(filterParameters.toString().
                        replace("[", "")
                        .replace("]", ""), "");
        ObjectNode resultNode = (ObjectNode) jsonNode;
        resultNode.put(NEXT_LINK, nextLinkCleaned);
        return resultNode.toPrettyString();
    }

    public  ResponseEntity<String> performCreateRequest(HttpServletRequest request, String body) {
        if (!keycloakUtils.isAdmin()) {
            if (staEntity.getClass().equals(Thing.class)) {
                return UNAUTHORIZED;
            } else {
                if (!isPrincipalTheThingOwner(request.getRequestURI(), keycloakUtils.getName())) {
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

    public ResponseEntity<String> performUpdateRequest(HttpServletRequest request, String body) throws RestClientException {
        if (keycloakUtils.isNotAuthenticated()) {
            return UNAUTHORIZED;
        }
        // Check whether requested resource references to the Keycloak id as it's Thing owner
        if (keycloakUtils.isAdmin()
                || isPrincipalTheThingOwner(request.getRequestURI(), keycloakUtils.getName())) {
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

    public boolean isPrincipalTheThingOwner(String requestUriString, String principalId) {
        try {
            var requestUri = buildOwnerRequestUri(requestUriString, principalId);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity<>(null, buildRequestHeaders()), String.class);
            if (response.hasBody()) {
                return (response.getBody().contains("@"));
            } else {
                return false;
            }

        } catch (HttpStatusCodeException e) {
            return false;
        }
    }

    public HttpHeaders buildRequestHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public URI buildUnfilteredUri(String requestUri, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam(FILTER, expand);
        }
        return uriComponentsBuilder.build().toUri();

    }

    public URI buildOwnerRequestUri(String requestUri, String userId) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildOwnerFilter(userId));
        return uriComponentsBuilder.build().toUri();
    }

    public URI buildPrivateRequestUri(String requestUri, String userId, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildOwnerFilter(userId) + " or " + buildPublicFilter() + " or " + buildConsumerFilter(userId));

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam(FILTER, expand);
        }

        return uriComponentsBuilder.build().toUri();
    }

    public URI buildPublicRequestUrl(String requestUri, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildPublicFilter());

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam("$expand", expand);
        }

        return uriComponentsBuilder.build().toUri();
    }

    public HttpHeaders getErrorHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    public String buildOwnerFilter(String userId) {
        return String.format("%s/%s eq '%s'", staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getOwnerProperty(), userId);
    }

    public String buildPublicFilter() {
        return String.format("%s/%s eq 'true'", staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getPublicProperty());
    }

    public String buildConsumerFilter(String userId) {
        return String.format("substringOf('\"%s\"',%s/%s)", userId, staEntity.getThingPropertyPath(), sensorThingsServiceProperties.getConsumerProperty());
    }


}
