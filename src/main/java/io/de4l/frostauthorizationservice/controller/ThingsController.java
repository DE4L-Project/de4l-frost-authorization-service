package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Thing;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Log4j2
@CrossOrigin(origins = "*")
public class ThingsController extends BaseRestController {

    public ThingsController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        super(sensorThingsServiceProperties, new Thing());
    }

    @GetMapping(value = "Things({id})", produces = "application/json")
    public String single(
            @PathVariable("id") String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return executeFrostRequest(request, token, expand);
    }

    @GetMapping(value = {"Datastreams({id})/Thing"}, produces = "application/json")
    public String datastreamsThing(
            @PathVariable("id") String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return executeFrostRequest(request, token, expand);
    }

    //@Secured({"admin", "user"})
    @GetMapping(value = "Things", produces = "application/json")
    public String list(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return executeFrostRequest(request, token, expand);
    }
}
