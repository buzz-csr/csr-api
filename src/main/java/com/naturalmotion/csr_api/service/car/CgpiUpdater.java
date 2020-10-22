package com.naturalmotion.csr_api.service.car;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

public class CgpiUpdater {

    public JsonArrayBuilder update(JsonArray cgpi, int size) {
        JsonArrayBuilder cgpiBuilder = Json.createArrayBuilder(cgpi);
        if (isLastGarageFull(cgpi)) {
            cgpiBuilder.add(size);
            cgpiBuilder.add(-1);
            cgpiBuilder.add(-1);
            cgpiBuilder.add(-1);
            cgpiBuilder.add(-1);
            cgpiBuilder.add(-1);
        } else {
            cgpiBuilder.set(size, size);
        }
        return cgpiBuilder;
    }

    private boolean isLastGarageFull(JsonArray cgpi) {
        return cgpi.getInt(cgpi.size() - 1) != -1;
    }

}
