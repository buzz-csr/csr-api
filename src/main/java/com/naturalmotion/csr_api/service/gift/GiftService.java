package com.naturalmotion.csr_api.service.gift;

import com.naturalmotion.csr_api.api.FusionColor;
import com.naturalmotion.csr_api.service.car.CarException;
import com.naturalmotion.csr_api.service.io.NsbException;

import javax.json.JsonObject;
import java.util.List;

public interface GiftService {

    JsonObject addEssence() throws CarException, NsbException;

    JsonObject addFusions(List<FusionColor> colors, List<String> brands);

    JsonObject addEliteToken();

    JsonObject addRestorationToken(String carId);

}
