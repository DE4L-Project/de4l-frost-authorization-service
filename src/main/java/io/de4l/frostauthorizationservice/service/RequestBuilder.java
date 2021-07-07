package io.de4l.frostauthorizationservice.service;

import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.StaEntity;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class RequestBuilder {

    private final SensorThingsServiceProperties sensorThingsServiceProperties;
    private final String FILTER;
    public RequestBuilder(SensorThingsServiceProperties sensorThingsServiceProperties) {
        this.sensorThingsServiceProperties = sensorThingsServiceProperties;
        this.FILTER = sensorThingsServiceProperties.getFILTER();
    }

    public URI buildUnfilteredUri(String requestUri, String expand) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam(FILTER, expand);
        }
        return uriComponentsBuilder.build().toUri();
    }

    public URI buildOwnerRequestUri(String requestUri, String userId, StaEntity staEntity) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildOwnerFilter(userId, staEntity));

        return uriComponentsBuilder.build().toUri();
    }

    public URI buildPrivateRequestUri(String requestUri, String userId, String expand, StaEntity staEntity) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildOwnerFilter(userId, staEntity) + " or " +
                buildPublicFilter(staEntity) + " or " + buildConsumerFilter(userId, staEntity));

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam(FILTER, expand);
        }
        return uriComponentsBuilder.build().toUri();
    }

    public URI buildPublicRequestUrl(String requestUri, String expand, StaEntity staEntity) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildPublicFilter(staEntity));

        if (Strings.isNotBlank(expand)) {
            uriComponentsBuilder.queryParam("$expand", expand);
        }
        return uriComponentsBuilder.build().toUri();
    }

    public String buildOwnerFilter(String userId, StaEntity staEntity) {
        return String.format("%s/%s eq '%s'", staEntity.getThingPropertyPath(),
                sensorThingsServiceProperties.getOwnerProperty(), userId);
    }

    public String buildPublicFilter(StaEntity staEntity) {
        return String.format("%s/%s eq 'true'", staEntity.getThingPropertyPath(),
                sensorThingsServiceProperties.getPublicProperty());
    }

    public String buildConsumerFilter(String userId, StaEntity staEntity) {
        return String.format("substringOf('%s',%s/%s)", userId, staEntity.getThingPropertyPath(),
                sensorThingsServiceProperties.getConsumerProperty());
    }

    public HttpHeaders getErrorHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    public HttpHeaders buildRequestHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
