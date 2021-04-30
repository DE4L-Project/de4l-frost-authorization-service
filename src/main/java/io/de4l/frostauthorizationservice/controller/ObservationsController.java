package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Observation;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ObservationsController extends BaseRestController {

    public ObservationsController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        super(sensorThingsServiceProperties, new Observation());
    }

    @GetMapping(value = "Observations({id})", produces = "application/json")
    public String single(
            @PathVariable("id") String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return executeFrostRequest(request, token, expand);
    }

    @GetMapping(value = "Observations", produces = "application/json")
    public String list(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return executeFrostRequest(request, token, expand);
    }

    @GetMapping(value = "Datastreams({id})/Observations", produces = "application/json")
    public String listForDatastream(
            @PathVariable("id") String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return executeFrostRequest(request, token, expand);
    }

}
