package com.naturalmotion.csr_api.service.check;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.naturalmotion.csr_api.service.car.FusionCalculator;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;

public class FusionChecker {

	private static final String UPST = "upst";
	private static final String CRDB = "crdb";
	private static final String CAOW = "caow";

	private JsonArray carlistFull;
	private FusionCalculator fusionCalculator = new FusionCalculator();

	public FusionChecker() throws NsbException {
		NsbReader nsbReader = new NsbReader();
		JsonObject nsbFull = nsbReader.getNsbFull();
		carlistFull = nsbFull.getJsonArray(CAOW);
	}

	public CheckReport checkCar(JsonObject car) throws NsbException {
		CheckReport report = null;

		JsonObject carFull = findCarFull(car);

		if (carFull != null) {
			int nbFusionFull = fusionCalculator.getFusionNumber(carFull);
			int nbFusion = fusionCalculator.getFusionNumber(car);
			if (nbFusion > nbFusionFull) {
				report = new CheckReport();
				report.setError(ErrorType.FUSION_MAX);
				if (car.getInt("cmlv") > 0) {
					report.setLevel(Level.WARN);
				} else {
					report.setLevel(Level.ERROR);
				}
				report.setMessage(
						car.getString(CRDB) + ": Maximum autorisé = " + nbFusionFull + ", actuellement = " + nbFusion);
			}
		}

		return report;
	}

	public JsonObject correct(JsonObject car) {
		JsonObject correctedCar = null;

		JsonObject carFull = findCarFull(car);

		if (carFull != null) {
			int nbFusionFull = fusionCalculator.getFusionNumber(carFull);
			int nbFusion = fusionCalculator.getFusionNumber(car);
			if (nbFusion > nbFusionFull) {
				JsonObjectBuilder newCar = Json.createObjectBuilder(car);
				JsonArray upstFull = carFull.getJsonArray(UPST);
				newCar.add(UPST, Json.createArrayBuilder(upstFull));
				correctedCar = newCar.build();
			} else {
				correctedCar = car;
			}
		} else {
			correctedCar = car;
		}
		return correctedCar;
	}

	private JsonObject findCarFull(JsonObject car) {
		JsonObject carFull = null;
		int i = 0;
		while (carFull == null && i < carlistFull.size()) {
			JsonObject actual = carlistFull.getJsonObject(i);
			if (actual.getString(CRDB).equals(car.getString(CRDB))) {
				carFull = actual;
			}
			i++;
		}
		return carFull;
	}

}
