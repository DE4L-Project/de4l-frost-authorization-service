package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Observation;
import io.de4l.frostauthorizationservice.security.KeycloakUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ObservationsController extends BaseRestController {

    public ObservationsController(SensorThingsServiceProperties sensorThingsServiceProperties, ObjectMapper objectMapper, KeycloakUtils keycloakUtils) {
        super(sensorThingsServiceProperties, new Observation(), objectMapper, keycloakUtils);
    }

    @GetMapping(value = {"Observations", "Observations({id})", "Datastreams({id})/Observations"}, produces = "application/json")
    public ResponseEntity<String> getObservation(
            @RequestParam(value = "$expand", required = false) String expand,
            HttpServletRequest request
    ) {
        return performReadRequest(request, expand);
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
            value = "Observations({id})", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateObservation(
            @RequestBody String body,
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, body);
    }

    @DeleteMapping(value = "Observations({id})", produces = "application/json")
    public ResponseEntity<String> deleteObservation(
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, null);
    }

    @PostMapping(value = "Datastreams({id})/Observations", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createObservation(
            @RequestBody String body,
            HttpServletRequest request
    ) {
        return performCreateRequest(request, body);
    }

}
