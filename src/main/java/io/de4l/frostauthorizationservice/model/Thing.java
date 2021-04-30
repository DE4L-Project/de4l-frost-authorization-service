package io.de4l.frostauthorizationservice.model;

public class Thing implements StaEntity {
    @Override
    public String getThingPropertyPath() {
        return "properties";
    }
}
