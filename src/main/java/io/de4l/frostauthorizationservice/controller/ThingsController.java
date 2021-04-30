package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.StatusCodeException;
import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.security.KeycloakPrincipal;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@Log4j2
@CrossOrigin(origins = "*")
public class ThingsController extends BaseRestController {

    public ThingsController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        super(sensorThingsServiceProperties);
    }

    @GetMapping(value = "Things", produces = "application/json")
    public String getThingsForUser(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken principal
    ) throws ServiceFailureException {
        KeycloakPrincipal keycloakPrincipal = new KeycloakPrincipal(principal);
        RestTemplate restTemplate = new RestTemplate();

        try {
            URI requestUri = this.buildFrostRequestUrl("Things", keycloakPrincipal.getUserId(), expand);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity(null, buildRequestHeaders(keycloakPrincipal.getJwtAuthenticationToken().getToken().getTokenValue())),
                    String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw e;
        }
    }

    @GetMapping(value = "Things({id})", produces = "application/json")
    public String getThingByIdForUser(
            @PathVariable("id") String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken principal
    ) throws ServiceFailureException {
        KeycloakPrincipal keycloakPrincipal = new KeycloakPrincipal(principal);
        RestTemplate restTemplate = new RestTemplate();

        try {
            URI requestUri = this.buildFrostRequestUrl("Things(" + id + ")", keycloakPrincipal.getUserId(), expand);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity(null, buildRequestHeaders(keycloakPrincipal.getJwtAuthenticationToken().getToken().getTokenValue())),
                    String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw e;
        }
    }

    @GetMapping(value = "Observations", produces = "application/json")
    public String getObservationsForUser(
            @RequestParam(value = "$expand", required = false) String expand
    ) throws ServiceFailureException, JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity requestEntity = new HttpEntity(null, headers);
//
////            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(staService.getEndpoint().toString() + "Observations")
////                    .queryParam("$filter", "Datastream/Thing/properties/ownerId eq 'gaunitz' or substringOf('gaunitz',Datastream/Thing/properties/sharedWith)");
//
////            URI requestUri = builder.build().toUri();
////            log.info(requestUri);
//            ResponseEntity<String> response = restTemplate.exchange(requestUri, HttpMethod.GET, requestEntity, String.class);
//            return response.getBody();
            return null;
        } catch (RestClientException e) {
            throw e;
        }
    }

    @ExceptionHandler(StatusCodeException.class)
    public ResponseEntity<Object> handleStaServiceException(StatusCodeException e) {
        log.error(e.getReturnedContent(), e);
        return ResponseEntity
                .status(e.getStatusCode())
                .body(e.getReturnedContent());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleRestClientException(HttpClientErrorException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(e.getStatusCode())
                .body(e.getResponseBodyAsString());
    }

}
