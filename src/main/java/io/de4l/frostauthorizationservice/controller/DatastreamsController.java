package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Datastream;
import io.de4l.frostauthorizationservice.security.KeycloakUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DatastreamsController extends BaseRestController {

    public DatastreamsController(SensorThingsServiceProperties sensorThingsServiceProperties, ObjectMapper objectMapper, KeycloakUtils keycloakUtils) {
        super(sensorThingsServiceProperties, new Datastream(), objectMapper, keycloakUtils);
    }

    @GetMapping(value = {"Datastreams({id})", "Datastreams", "Things({id})/Datastreams"}, produces = "application/json")
    public ResponseEntity<String> specificDatastream(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performReadRequest(request, token, expand);
    }

    @RequestMapping(method = {RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH},
            value = "Datastreams({id})", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateDatastream(
            @RequestBody String body,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, token, body);
    }

    @PostMapping(value = "Datastreams", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createDatastream(
            @RequestBody String body,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performCreateRequest(request, token, body);
    }

}
