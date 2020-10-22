package com.naturalmotion.csr_api.service.car;

import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;

import javax.json.*;

public class CidcUpdater {

    private NsbReader nsbReader = new NsbReader();

    public JsonArrayBuilder update(String carId, JsonObject nsb) throws NsbException {
        JsonObject nsbFull = nsbReader.getNsbFull();
        JsonArray cidcFull = nsbFull.getJsonArray("cidc");

        JsonArray cidc = nsb.getJsonArray("cidc");
        JsonArrayBuilder cidcBuilder = Json.createArrayBuilder(cidc);
        JsonValue jsonValue = cidc.stream().filter(x -> ((JsonString)x).getString().equals(carId)).findFirst().orElse(null);
        if (jsonValue == null) {
            cidcFull.forEach(x -> {
                if (((JsonString)x).getString().equals(carId)) {
                    cidcBuilder.add(carId);
                }
            });
        }
        return cidcBuilder;
    }
}
