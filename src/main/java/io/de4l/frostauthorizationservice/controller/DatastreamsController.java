package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Datastream;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DatastreamsController extends BaseRestController {

    public DatastreamsController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        super(sensorThingsServiceProperties, new Datastream());
    }

    @GetMapping(value = {"Datastreams({id})", "Datastreams", "Things({id})/Datastreams"}, produces = "application/json")
    public ResponseEntity<String> specificDatastream(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performReadRequest(request, token, expand);
    }

}
