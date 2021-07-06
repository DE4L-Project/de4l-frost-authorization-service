package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Thing;
import io.de4l.frostauthorizationservice.security.KeycloakUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@Log4j2
@CrossOrigin(origins = "*")
public class ThingsController extends BaseRestController {

    public ThingsController(SensorThingsServiceProperties sensorThingsServiceProperties, ObjectMapper objectMapper, KeycloakUtils keycloakUtils) {
        super(sensorThingsServiceProperties, new Thing(), objectMapper, keycloakUtils);
    }

    @GetMapping(value = {"Things", "Things({id})", "Datastreams({id})/Thing"}, produces = "application/json")
    public ResponseEntity<String> readThing(
            @RequestParam(value = "$expand", required = false) String expand,
            HttpServletRequest request
    ) {
        return performReadRequest(request, expand);
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
            value = "Things({id})", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateThing(
            @RequestBody String body,
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, body);
    }

    @DeleteMapping(value = "Things({id})", produces = "application/json")
    public ResponseEntity<String> deleteThing(
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, null);
    }

    @PostMapping(value = "Things", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createThing(
            @RequestBody String body,
            HttpServletRequest request
    ) {
        return performCreateRequest(request, body);
    }

}
