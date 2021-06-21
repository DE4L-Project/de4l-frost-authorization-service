package io.de4l.frostauthorizationservice.controller;

import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Thing;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = {"Things", "Things({id})", "Datastreams({id})/Thing"}, produces = "application/json")
    public ResponseEntity<String> readThing(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performReadRequest(request, token, expand);
    }

    @RequestMapping(method = {RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH},
            value = "Things({id})", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateThing(
            @RequestBody String body,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performUpdateRequest(request, token, body);
    }

    // Accessible to admin only via Spring Security
    @PostMapping(value = "Things", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createThing(
            @RequestBody String body,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performCreateRequest(request, token, body);
    }

}
