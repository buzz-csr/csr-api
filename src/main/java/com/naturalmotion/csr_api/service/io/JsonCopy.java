package com.naturalmotion.csr_api.service.io;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.Map;

public class JsonCopy {

    public JsonObjectBuilder copyObject(JsonObject nsbObject) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        for (Map.Entry<String, JsonValue> entry : nsbObject.entrySet()) {
            objectBuilder.add(entry.getKey(), entry.getValue());
        }
        return objectBuilder;
    }
}
