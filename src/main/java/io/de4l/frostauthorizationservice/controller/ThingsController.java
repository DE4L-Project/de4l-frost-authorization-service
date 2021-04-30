package io.de4l.frostauthorizationservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import io.de4l.frostauthorizationservice.controller.sta.Thing;
import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.security.KeycloakPrincipal;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

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
            HttpServletRequest request,
            JwtAuthenticationToken token
    ) {
        return executeFrostRequest(id, token, expand);
    }

    @GetMapping(value = "Things", produces = "application/json")
    public String list(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token
    ) {
        return executeFrostRequest(token, expand);
    }
}
