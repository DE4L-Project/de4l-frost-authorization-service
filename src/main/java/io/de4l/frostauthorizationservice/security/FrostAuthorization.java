package io.de4l.frostauthorizationservice.security;

import io.de4l.frostauthorizationservice.model.StaEntity;
import io.de4l.frostauthorizationservice.service.RequestBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class FrostAuthorization {

    private RequestBuilder requestBuilder;
    private RestTemplate restTemplate;

    public FrostAuthorization(RequestBuilder requestBuilder, RestTemplate restTemplate) {
        this.requestBuilder = requestBuilder;
        this.restTemplate = restTemplate;
    }

    public boolean isPrincipalTheThingOwner(String requestUriString, String principalId, StaEntity staEntity) {
        try {
            var requestUri = requestBuilder.buildOwnerRequestUri(requestUriString, principalId, staEntity);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUri,
                    HttpMethod.GET,
                    new HttpEntity<>(null, requestBuilder.buildRequestHeaders()), String.class);
            if (response.hasBody()) {
                return (response.getBody().contains("@"));
            } else {
                return false;
            }

        } catch (HttpStatusCodeException e) {
            return false;
        }
    }
}
