package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Observation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ObservationsController extends BaseRestController {

    public ObservationsController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        super(sensorThingsServiceProperties, new Observation());
    }

    @GetMapping(value = {"Observations", "Observations({id})", "Datastreams({id})/Observations"}, produces = "application/json")
    public ResponseEntity<String> getObservation(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performReadRequest(request, token, expand);
    }

    @RequestMapping(method = {RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH},
            value = "Observations({id})", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateThing(
            @RequestBody String body,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, token, body);
    }

    @PostMapping(value = "Datastreams({id})/Observations", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createObservation(
            @RequestBody String body,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performCreateRequest(request, token, body);
    }

}
