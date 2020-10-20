package com.naturalmotion.csr_api.service.gift;

import java.util.List;

import javax.json.JsonObject;

import com.naturalmotion.csr_api.api.EliteTokenParam;
import com.naturalmotion.csr_api.api.FusionColor;
import com.naturalmotion.csr_api.service.car.CarException;
import com.naturalmotion.csr_api.service.io.NsbException;

public interface GiftService {

    JsonObject addEssence() throws CarException, NsbException;

    JsonObject addFusions(List<FusionColor> colors, List<String> brands) throws NsbException;

    JsonObject addEliteToken(List<EliteTokenParam> tokenParam) throws NsbException;

    JsonObject addRestorationToken(String carId);

}
