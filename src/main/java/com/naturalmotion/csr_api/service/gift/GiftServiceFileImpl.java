package com.naturalmotion.csr_api.service.gift;

import com.naturalmotion.csr_api.api.CarElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.naturalmotion.csr_api.api.EliteTokenParam;
import com.naturalmotion.csr_api.api.FusionColor;
import com.naturalmotion.csr_api.service.io.JsonCopy;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;
import com.naturalmotion.csr_api.service.io.NsbWriter;

import java.util.ArrayList;
import java.util.Arrays;
public class GiftServiceFileImpl implements GiftService {

    private NsbWriter nsbWriter = new NsbWriter();

    private NsbReader nsbReader = new NsbReader();

    private JsonCopy jsonCopy = new JsonCopy();

    private GiftBuilder builder = new GiftBuilder();

    private final String path;

    public GiftServiceFileImpl(String path) {
        this.path = path;
    }

    @Override
    public JsonObject addEssence() throws NsbException {
        JsonObjectBuilder gift = builder.buildEssence("0_0");

        File nsb = nsbReader.getNsbFile(path);
        JsonObject nsbObject = nsbReader.readJsonObject(nsb);

        JsonObjectBuilder newNsb = addGift(Arrays.asList(gift), nsbObject);
        nsbWriter.writeNsb(nsb, newNsb);
        return newNsb.build();
    }

    @Override
    public JsonObject addFusions(List<FusionColor> colors, List<String> brands) throws NsbException {
        List<JsonObjectBuilder> gifts = new ArrayList<>();
        int index = 0;
        for (String brand : brands) {
            for (FusionColor color : colors) {
                for (CarElement element : CarElement.values()) {
                    gifts.add(builder.buildFusion(String.valueOf(index++), brand, element, color, 10));
                }
            }
        }

        File nsb = nsbReader.getNsbFile(path);
        JsonObject nsbObject = nsbReader.readJsonObject(nsb);

        JsonObjectBuilder newNsb = addGift(gifts, nsbObject);
        nsbWriter.writeNsb(nsb, newNsb);
        return newNsb.build();
    }

    @Override
    public JsonObject addEliteToken(List<EliteTokenParam> tokenParams) throws NsbException {
        List<JsonObjectBuilder> gifts = new ArrayList<>();
        for (EliteTokenParam param : tokenParams) {
            gifts.add(builder.buildEliteToken(param.getToken(), param.getAmount()));
        }

        return null;
    }

    @Override
    public JsonObject addRestorationToken(String carId) {
        return null;
    }

    private JsonObjectBuilder addGift(List<JsonObjectBuilder> gifts, JsonObject nsbObject) {
        JsonObjectBuilder newNsb = jsonCopy.copyObject(nsbObject);

        JsonArray picl = nsbObject.getJsonArray("picl");
        JsonArrayBuilder piclBuilder = Json.createArrayBuilder(picl);
        JsonArray playinbitms = nsbObject.getJsonArray("playinbitms");
        JsonArrayBuilder itmsBuilder = Json.createArrayBuilder(playinbitms);

        for (JsonObjectBuilder gift : gifts) {
            piclBuilder.add("eRewards");
            itmsBuilder.add(gift);
        }
        newNsb.add("picl", piclBuilder);
        newNsb.add("playinbitms", itmsBuilder);

        return newNsb;
    }
}
