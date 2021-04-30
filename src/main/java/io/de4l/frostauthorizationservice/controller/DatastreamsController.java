package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.controller.sta.Datastream;
import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatastreamsController extends BaseRestController {

    public DatastreamsController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        super(sensorThingsServiceProperties, new Datastream());
    }

    @GetMapping(value = "Datastreams({id})", produces = "application/json")
    public String single(
            @PathVariable("id") String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token
    ) {
        return executeFrostRequest(id, token, expand);
    }

    @GetMapping(value = "Datastreams", produces = "application/json")
    public String list(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token
    ) {
        return executeFrostRequest(token, expand);
    }

}
