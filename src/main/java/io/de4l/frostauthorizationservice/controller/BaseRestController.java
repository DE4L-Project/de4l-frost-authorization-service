package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.StaEntity;
import io.de4l.frostauthorizationservice.model.Thing;
import io.de4l.frostauthorizationservice.security.FrostAuthorization;
import io.de4l.frostauthorizationservice.security.KeycloakUtils;
import io.de4l.frostauthorizationservice.service.RequestBuilder;
import io.de4l.frostauthorizationservice.service.ResponseCleaner;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RequestMapping(path = "/FROST-Server/v1.0/")
@CrossOrigin(origins = "*")
@Log4j2
public abstract class BaseRestController {
    protected final SensorThingsServiceProperties sensorThingsServiceProperties;
    protected final StaEntity staEntity;
    protected final KeycloakUtils keycloakUtils;
    protected final RequestBuilder requestBuilder;
    protected final ResponseCleaner responseCleaner;
    protected final FrostAuthorization frostAuthorization;
    private final ResponseEntity<String> UNAUTHORIZED;
    private final ResponseEntity<String> NOTHING_FOUND;
    private final String EXCEPTION_MESSAGE = "message";

    @Autowired
    private RestTemplate restTemplate;

    protected BaseRestController(SensorThingsServiceProperties sensorThingsServiceProperties,
                                 StaEntity staEntity, KeycloakUtils keycloakUtils, RequestBuilder requestBuilder,
                                 ResponseCleaner responseCleaner, FrostAuthorization frostAuthorization) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
        this.staEntity = staEntity;
        this.keycloakUtils = keycloakUtils;
        this.requestBuilder = requestBuilder;
        this.responseCleaner = responseCleaner;
        this.frostAuthorization = frostAuthorization;
        Map<String,Object> unauthorizedMessage = new HashMap<>();
        unauthorizedMessage.put(EXCEPTION_MESSAGE, "Unauthorized");
        UNAUTHORIZED = new ResponseEntity<>(String.valueOf(new JSONObject(unauthorizedMessage)), HttpStatus.UNAUTHORIZED);
        Map<String,Object> nothingFoundMessage = new HashMap<>();
        nothingFoundMessage.put(EXCEPTION_MESSAGE, "Nothing Found");
        NOTHING_FOUND = new ResponseEntity<>(String.valueOf(new JSONObject(nothingFoundMessage)), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> performReadRequest(HttpServletRequest request, MultiValueMap<String, String> requestParameters) throws RestClientException, JsonProcessingException {
        URI requestUri;
        if (keycloakUtils.isNotAuthenticated()) {
            // Public Request
            requestUri = requestBuilder.buildPublicRequestUrl(request.getRequestURI(), requestParameters, staEntity);
        } else {
            // Potential authenticated Request
            if (keycloakUtils.isAdmin()) {
                // Admin request
                requestUri = requestBuilder.buildUnfilteredUri(request.getRequestURI(), requestParameters);
            } else {
                // Consumer/Owner request
                requestUri = requestBuilder.buildPrivateRequestUri(request.getRequestURI(), keycloakUtils.getName(), requestParameters, staEntity);
            }
        }
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity<String>(null, requestBuilder.buildRequestHeaders()), String.class);
            var cleanedResponse = responseCleaner.removeFilterFromResponse(response.getBody());
            return new ResponseEntity<>(cleanedResponse, response.getStatusCode());
    }

    public  ResponseEntity<String> performCreateRequest(HttpServletRequest request, String body) {
        if (!keycloakUtils.isAdmin()) {
            if (staEntity.getClass().equals(Thing.class)) {
                return UNAUTHORIZED;
            } else {
                if (!frostAuthorization.isPrincipalTheThingOwner(request.getRequestURI(), keycloakUtils.getName(), staEntity)) {
                    return NOTHING_FOUND;
                }
            }
        }
        ResponseEntity<String> response = restTemplate.exchange(
                requestBuilder.buildUnfilteredUri(request.getRequestURI(), null),
                HttpMethod.POST,
                new HttpEntity<>(body, requestBuilder.buildRequestHeaders()), String.class);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    public ResponseEntity<String> performUpdateRequest(HttpServletRequest request, String body) throws RestClientException {
        if (keycloakUtils.isNotAuthenticated()) {
            return UNAUTHORIZED;
        }
        // Check whether requested resource references to the Keycloak id as it's Thing owner
        if (keycloakUtils.isAdmin()
                || frostAuthorization.isPrincipalTheThingOwner(request.getRequestURI(), keycloakUtils.getName(), staEntity)) {
            var response = restTemplate.exchange(
                    requestBuilder.buildUnfilteredUri(request.getRequestURI(), null),
                    HttpMethod.valueOf(request.getMethod()),
                    new HttpEntity<>(body, requestBuilder.buildRequestHeaders()), String.class);
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } else {
            return NOTHING_FOUND;
        }
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleRestClientException(HttpClientErrorException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getResponseBodyAsString(), requestBuilder.getErrorHttpHeaders(), e.getStatusCode());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException e) {
        Map<String, Object> exceptionObject = new HashMap<>();
        exceptionObject.put(EXCEPTION_MESSAGE, e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(exceptionObject, requestBuilder.getErrorHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
