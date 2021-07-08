package io.de4l.frostauthorizationservice.service;

import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import io.de4l.frostauthorizationservice.model.StaEntity;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
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

    public URI buildUnfilteredUri(String requestUri, MultiValueMap<String, String> requestParameters) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);

        appendQueryParams(uriComponentsBuilder, requestParameters, "");

        return uriComponentsBuilder.build().toUri();
    }

    private void appendQueryParams(UriComponentsBuilder uriComponentsBuilder, MultiValueMap<String, String> queryParameters,
                                                   String authorizationFilter) {
        if (queryParameters == null) {
            return;
        }
        var queryOptions = new String[]{"$expand", "$top", "$count", "$select", "$orderby", "$skip", "$resultFormat"};
        for (String queryOption : queryOptions) {
            if (Strings.isNotBlank(queryParameters.getFirst(queryOption))) {
                uriComponentsBuilder.queryParam(queryOption, queryParameters.getFirst(queryOption));
            }
        }

        if (authorizationFilter.isEmpty()) {
            if (queryParameters.containsKey(FILTER)) {
                uriComponentsBuilder.queryParam(FILTER, queryParameters.getFirst(FILTER));
            }
        } else {
            if (queryParameters.containsKey(FILTER)) {
                String concatenatedRequest = authorizationFilter.concat(" and " + queryParameters.getFirst(FILTER));
                uriComponentsBuilder.queryParam(FILTER, concatenatedRequest);
            } else {
                uriComponentsBuilder.queryParam(FILTER, authorizationFilter);
            }
        }
    }

    public URI buildOwnerRequestUri(String requestUri, String userId, StaEntity staEntity) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        uriComponentsBuilder.queryParam(FILTER, buildOwnerFilter(userId, staEntity));

        return uriComponentsBuilder.build().toUri();
    }

    public URI buildPrivateRequestUri(String requestUri, String userId, MultiValueMap<String, String> requestParameters, StaEntity staEntity) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        var authenticationFilter = buildOwnerFilter(userId, staEntity) + " or " +
                buildPublicFilter(staEntity) + " or " + buildConsumerFilter(userId, staEntity);
       appendQueryParams(uriComponentsBuilder, requestParameters, authenticationFilter);
        return uriComponentsBuilder.build().toUri();
    }

    public URI buildPublicRequestUrl(String requestUri, MultiValueMap<String, String> requestParameters, StaEntity staEntity) {
        var uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(sensorThingsServiceProperties.getFrostUri() + requestUri);
        var authenticationFilter = buildPublicFilter(staEntity);
        appendQueryParams(uriComponentsBuilder, requestParameters, authenticationFilter);

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
