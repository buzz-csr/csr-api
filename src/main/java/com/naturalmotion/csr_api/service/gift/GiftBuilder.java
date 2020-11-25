package com.naturalmotion.csr_api.service.gift;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import com.naturalmotion.csr_api.api.CarElement;
import com.naturalmotion.csr_api.api.EliteToken;
import com.naturalmotion.csr_api.api.FusionColor;

import java.math.BigDecimal;

public class GiftBuilder {

	public JsonObjectBuilder buildFusion(String id, String brand, CarElement element, FusionColor color,
			BigDecimal amount) {
		return build(id, 13, brand, element.getPartType(), color.getGrade(), amount.intValue());
	}

	public JsonObjectBuilder buildEssence(String id, BigDecimal qty) {
		return build(id, 10, "", 7, 0, qty.intValue());
	}

	public JsonObjectBuilder buildEliteToken(EliteToken token, BigDecimal amount) {
		return build("1592077438_24_0", 24, "", 7, token.getGrade(), amount.intValue());
	}

	public JsonObjectBuilder buildStage6(String carId, int type) {
		return build("0_2", 12, carId, type, 0, 1);
	}

	public JsonObjectBuilder buildRestorationToken(String carId, BigDecimal amount) {
		return build("0_1", 22, carId, 7, 0, amount.intValue());
	}

	private JsonObjectBuilder build(String id, int type, String name, int part, int grade, int amount) {
		JsonObjectBuilder gift = Json.createObjectBuilder();

		gift.add("reason", "TEXT_TAG_CS_COMPENSATION");
		gift.add("ttl", 0);
		gift.add("id", id);
		gift.add("rank", 0);
		gift.add("CSR2ApplyableReward", buildReward(type, name, part, grade, amount));
		return gift;
	}

	private JsonObjectBuilder buildReward(int type, String name, int part, int grade, int amount) {
		JsonObjectBuilder reward = Json.createObjectBuilder();
		reward.add("rewardType", type);
		reward.add("name", name);
		reward.add("partType", part);
		reward.add("partGrade", grade);
		reward.add("gachaConfig", -1);
		reward.add("amount", amount);
		return reward;
	}
}
