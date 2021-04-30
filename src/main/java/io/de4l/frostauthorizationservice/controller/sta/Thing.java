package io.de4l.frostauthorizationservice.controller.sta;

public class Thing extends StaEntity {
    @Override
    public String getUrlPath() {
        return "Things";
    }

    @Override
    public String getThingPropertyPath() {
        return "properties";
    }
}
