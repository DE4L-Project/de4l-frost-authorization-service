package io.de4l.frostauthorizationservice.controller.sta;

public class Observation extends StaEntity {
    @Override
    public String getUrlPath() {
        return "Observations";
    }

    @Override
    public String getThingPropertyPath() {
        return "Datastream/Thing/properties";
    }
}
