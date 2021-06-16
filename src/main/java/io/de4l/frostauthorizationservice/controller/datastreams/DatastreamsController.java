package io.de4l.frostauthorizationservice.controller.datastreams;

import io.de4l.frostauthorizationservice.controller.BaseRestController;
import io.de4l.frostauthorizationservice.frost.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.Datastream;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DatastreamsController extends BaseRestController {

    public DatastreamsController(SensorThingsServiceProperties sensorThingsServiceProperties) {
        super(sensorThingsServiceProperties, new Datastream());
    }


    @GetMapping(value = {"Datastreams({id})" }, produces = "application/json")
    public String specificDatastream(
            @PathVariable(value = "id", required = false) String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performReadRequest(request, token, expand);
    }

    @GetMapping(value = { "Datastreams"}, produces = "application/json")
    public String allDatastreams(
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performReadRequest(request, token, expand);
    }

    @GetMapping(value = { "Things({id})/Datastreams"}, produces = "application/json")
    public String datastreamsOfOneThing(
            @PathVariable(value = "id", required = false) String id,
            @RequestParam(value = "$expand", required = false) String expand,
            JwtAuthenticationToken token,
            HttpServletRequest request
    ) {
        return performReadRequest(request, token, expand);
    }
}
