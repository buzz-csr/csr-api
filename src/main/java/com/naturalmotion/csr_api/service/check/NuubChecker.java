package com.naturalmotion.csr_api.service.check;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.naturalmotion.csr_api.service.car.CarUpgradeCalculator;

/**
 * Contrôle le champ nuub = Nombre de pièces achetées
 */
public class NuubChecker {

	private static final String NUUB = "nuub";

	public CheckReport checkCar(JsonObject car) {
		CheckReport checkReport = null;
		int nbUpgradeBuy = new CarUpgradeCalculator().compute(car);

		int actualupgradeBuy = car.getInt(NUUB);
		if (actualupgradeBuy != nbUpgradeBuy) {
			checkReport = new CheckReport();
			checkReport.setError(ErrorType.WRONG_NUUB);
			checkReport.setLevel(Level.ERROR);
			checkReport.setMessage(
					car.getString("crdb") + ": Actualement=" + actualupgradeBuy + ", attendu=" + nbUpgradeBuy);
		}
		return checkReport;
	}

	public JsonObject correct(JsonObject car) {
		JsonObject correctedCar = null;
		int nbUpgradeBuy = new CarUpgradeCalculator().compute(car);

		int actualupgradeBuy = car.getInt(NUUB);
		if (actualupgradeBuy != nbUpgradeBuy) {
			JsonObjectBuilder newCar = Json.createObjectBuilder(car);
			newCar.add(NUUB, nbUpgradeBuy);
			correctedCar = newCar.build();
		} else {
			correctedCar = car;
		}
		return correctedCar;
	}
}
