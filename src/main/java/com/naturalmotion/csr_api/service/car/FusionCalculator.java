package com.naturalmotion.csr_api.service.car;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class FusionCalculator {

	private static final String UPST = "upst";

	public int getFusionNumber(JsonObject carFull) {
		int nbFusion = 0;
		JsonArray parts = carFull.getJsonArray(UPST);
		for (int p = 0; p < parts.size(); p++) {
			JsonObject part = parts.getJsonObject(p);
			JsonArray partLevels = part.getJsonArray("lvls");
			for (int l = 0; l < partLevels.size(); l++) {
				JsonObject level = partLevels.getJsonObject(l);
				JsonArray fusions = level.getJsonArray("fsg");
				for (int f = 0; f < fusions.size(); f++) {
					int fusion = fusions.getInt(f);
					if (fusion > 0) {
						nbFusion++;
					}
				}
			}
		}
		return nbFusion;
	}
}
