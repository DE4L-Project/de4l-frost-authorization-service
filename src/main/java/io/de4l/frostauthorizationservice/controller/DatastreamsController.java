package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Datastream;
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
public class DatastreamsController extends BaseRestController {

    public DatastreamsController(SensorThingsServiceProperties sensorThingsServiceProperties,
                                 KeycloakUtils keycloakUtils, RequestBuilder urlBuilder,
                                 ResponseCleaner responseCleaner, FrostAuthorization frostAuthorization) {
        super(sensorThingsServiceProperties, new Datastream(), keycloakUtils, urlBuilder, responseCleaner, frostAuthorization);
    }

    @GetMapping(value = {"Datastreams({id})", "Datastreams", "Things({id})/Datastreams"}, produces = "application/json")
    public ResponseEntity<String> getDatastream(
            @RequestParam(value = "$expand", required = false) String expand,
            HttpServletRequest request
    ) throws JsonProcessingException {
        return performReadRequest(request, expand);
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
            value = "Datastreams({id})", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateDatastream(
            @RequestBody String body,
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, body);
    }

    @DeleteMapping(value = "Datastreams({id})", produces = "application/json")
    public ResponseEntity<String> deleteDatastream(
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, null);
    }

    @PostMapping(value = "Things({id})/Datastreams", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createDatastream(
            @RequestBody String body,
            HttpServletRequest request
    ) {
        return performCreateRequest(request, body);
    }


}
