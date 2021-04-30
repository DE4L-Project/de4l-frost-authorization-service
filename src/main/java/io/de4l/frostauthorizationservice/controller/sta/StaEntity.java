package io.de4l.frostauthorizationservice.controller.sta;

import org.apache.logging.log4j.util.Strings;

public abstract class StaEntity {

    public abstract String getUrlPath();

    public String getUrlPath(String id) {
        if (Strings.isNotBlank(id)) {
            return this.getUrlPath() + "(" + id + ")";
        }
        return this.getUrlPath();
    }

    public abstract String getThingPropertyPath();
}
