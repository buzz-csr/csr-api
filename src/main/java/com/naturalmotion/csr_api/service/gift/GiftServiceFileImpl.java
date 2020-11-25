package com.naturalmotion.csr_api.service.gift;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.naturalmotion.csr_api.api.CarElement;
import com.naturalmotion.csr_api.api.EliteTokenParam;
import com.naturalmotion.csr_api.api.FusionParam;
import com.naturalmotion.csr_api.service.io.JsonBuilder;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;
import com.naturalmotion.csr_api.service.io.ProfileFileWriter;

public class GiftServiceFileImpl implements GiftService {

	private ProfileFileWriter nsbWriter = new ProfileFileWriter();

	private NsbReader nsbReader = new NsbReader();

	private GiftBuilder builder = new GiftBuilder();

	private JsonBuilder jsonBuilder = new JsonBuilder();

	private final String path;

	public GiftServiceFileImpl(String path) {
		this.path = path;
	}

	@Override
	public JsonObject addEssence(BigDecimal qty) throws NsbException {
		JsonObjectBuilder gift = builder.buildEssence("0_0", qty);

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);

		JsonObjectBuilder newNsb = addGift(Arrays.asList(gift), nsbObject);
		nsbWriter.write(nsb, newNsb);
		return newNsb.build();
	}

	@Override
	public JsonObject addFusions(List<FusionParam> colors, List<String> brands) throws NsbException {
		List<JsonObjectBuilder> gifts = new ArrayList<>();
		int index = 0;
		for (String brand : brands) {
			for (FusionParam fusion : colors) {
				for (CarElement element : CarElement.values()) {
					gifts.add(builder.buildFusion(String.valueOf(index++), brand, element, fusion.getColor(),
							fusion.getQuantity()));
				}
			}
		}

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);

		JsonObjectBuilder newNsb = addGift(gifts, nsbObject);
		nsbWriter.write(nsb, newNsb);
		return newNsb.build();
	}

	@Override
	public JsonObject addEliteToken(List<EliteTokenParam> tokenParams) throws NsbException {
		List<JsonObjectBuilder> gifts = new ArrayList<>();
		for (EliteTokenParam param : tokenParams) {
			gifts.add(builder.buildEliteToken(param.getToken(), param.getAmount()));
		}

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);

		JsonObjectBuilder newNsb = addGift(gifts, nsbObject);
		nsbWriter.write(nsb, newNsb);
		return newNsb.build();
	}

	@Override
	public JsonObject addRestorationToken(String carId, BigDecimal amount) throws NsbException {
		List<JsonObjectBuilder> gifts = new ArrayList<>();
		gifts.add(builder.buildRestorationToken(carId, amount));

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);

		JsonObjectBuilder newNsb = addGift(gifts, nsbObject);
		nsbWriter.write(nsb, newNsb);
		return newNsb.build();
	}

	@Override
	public JsonObject addStage6(String carId) throws NsbException {
		List<JsonObjectBuilder> gifts = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			gifts.add(builder.buildStage6(carId, i));
		}
		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);

		JsonObjectBuilder newNsb = addGift(gifts, nsbObject);
		nsbWriter.write(nsb, newNsb);
		return newNsb.build();
	}

	private JsonObjectBuilder addGift(List<JsonObjectBuilder> gifts, JsonObject nsbObject) {
		JsonObjectBuilder newNsb = Json.createObjectBuilder(nsbObject);

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
