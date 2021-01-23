package com.naturalmotion.csr_api.service.car;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class CarUpgradeCalculator {

	public int compute(JsonObject car) {
		int nbUpgradeBuy = 0;
		JsonArray parts = car.getJsonArray("upst");

		int partPos = 0;
		while (partPos < parts.size()) {
			JsonObject part = parts.getJsonObject(partPos);
			int levelInstalled = part.getInt("lvlo");
			if (levelInstalled > 0) {
				nbUpgradeBuy += Math.min(5, levelInstalled);
			}
			partPos++;
		}
		return nbUpgradeBuy;
	}
}
