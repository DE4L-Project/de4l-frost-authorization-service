package io.de4l.frostauthorizationservice.model;

public class Observation implements StaEntity {

    @Override
    public String getThingPropertyPath() {
        return "Datastream/Thing/properties";
    }
}
