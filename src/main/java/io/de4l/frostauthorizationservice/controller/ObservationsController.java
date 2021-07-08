package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Observation;
import io.de4l.frostauthorizationservice.security.FrostAuthorization;
import io.de4l.frostauthorizationservice.security.KeycloakUtils;
import io.de4l.frostauthorizationservice.service.RequestBuilder;
import io.de4l.frostauthorizationservice.service.ResponseCleaner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ObservationsController extends BaseRestController {

    public ObservationsController(SensorThingsServiceProperties sensorThingsServiceProperties,
                                  KeycloakUtils keycloakUtils, RequestBuilder urlBuilder, ResponseCleaner responseCleaner,
                                  FrostAuthorization frostAuthorization) {
        super(sensorThingsServiceProperties, new Observation(), keycloakUtils, urlBuilder, responseCleaner, frostAuthorization);
    }

    @GetMapping(value = {"Observations", "Observations({id})", "Datastreams({id})/Observations"}, produces = "application/json")
    public ResponseEntity<String> getObservation(
            @RequestParam(value = "$expand", required = false) String expand,
            HttpServletRequest request
    ) throws JsonProcessingException {
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
