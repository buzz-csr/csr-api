package com.naturalmotion.csr_api.service.car;

import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;

import javax.json.*;

public class Cid2Updater {

    private NsbReader nsbReader = new NsbReader();

    public JsonArrayBuilder update(String carId, JsonObject nsb) throws NsbException {
        JsonObject nsbFull = nsbReader.getNsbFull();
        JsonArray cid2Full = nsbFull.getJsonArray("cid2");

        JsonArray cid2 = nsb.getJsonArray("cid2");
        JsonArrayBuilder cid2Builder = Json.createArrayBuilder(cid2);
        JsonValue jsonValue = cid2.stream().filter(x -> x.asJsonObject().getString("id").equals(carId))
                .findFirst().orElse(null);
        if (jsonValue == null) {
            cid2Full.forEach(x -> {
                if (x.asJsonObject().getString("id").equals(carId)) {
                    JsonObjectBuilder object = Json.createObjectBuilder();
                    object.add("id", carId);
                    object.add("ct", 1);
                    cid2Builder.add(object);
                }
            });
        }
        return cid2Builder;
    }
}
