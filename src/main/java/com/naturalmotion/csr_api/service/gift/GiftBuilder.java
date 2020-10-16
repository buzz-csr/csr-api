package com.naturalmotion.csr_api.service.gift;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class GiftBuilder {

    public JsonObjectBuilder buildEssence(String id) {
        return build(id, 10, "", 7, 0, 2000);
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
