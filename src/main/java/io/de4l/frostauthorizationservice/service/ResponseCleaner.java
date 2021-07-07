package io.de4l.frostauthorizationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.de4l.frostauthorizationservice.config.SensorThingsServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ResponseCleaner {

    @Autowired
    private ObjectMapper objectMapper;
    private String FILTER;

    ResponseCleaner(ObjectMapper objectMapper, SensorThingsServiceProperties sensorThingsServiceProperties) {
        this.FILTER = sensorThingsServiceProperties.getFILTER();
        this.objectMapper = objectMapper;
    }

    public String removeFilterFromResponse(String response) throws JsonProcessingException {
        final var NEXT_LINK = "@iot.nextLink";
        var jsonNode = objectMapper.readTree(response);
        var nextLink = jsonNode.at("/" + NEXT_LINK).asText();
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(nextLink).build().getQueryParams();
        List<String> filterParameters = parameters.get(FILTER);
        if (filterParameters == null) {
            return response;
        }
        var nextLinkCleaned = nextLink
                .replace("&"+FILTER+"=", "")
                .replace(filterParameters.toString().
                        replace("[", "")
                        .replace("]", ""), "");
        ObjectNode resultNode = (ObjectNode) jsonNode;
        resultNode.put(NEXT_LINK, nextLinkCleaned);
        return resultNode.toPrettyString();
    }
}
