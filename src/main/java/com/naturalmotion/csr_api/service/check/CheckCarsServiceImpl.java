package com.naturalmotion.csr_api.service.check;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.naturalmotion.csr_api.service.car.CarUpgradeCalculator;
import com.naturalmotion.csr_api.service.io.JsonBuilder;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;
import com.naturalmotion.csr_api.service.io.ProfileFileWriter;

public class CheckCarsServiceImpl implements CheckCarsService {

	private static final String CAOW = "caow";

	private static final String NUUB = "nuub";

	private NsbReader nsbReader = new NsbReader();

	private JsonBuilder jsonBuilder = new JsonBuilder();

	private ProfileFileWriter fileWriter = new ProfileFileWriter();

	@Override
	public List<CheckReport> check(String path) throws NsbException {
		List<CheckReport> reports = new ArrayList<>();

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		int pos = 0;
		while (pos < caowObject.size()) {

			JsonObject car = caowObject.getJsonObject(pos);
			int nbUpgradeBuy = new CarUpgradeCalculator().compute(car);

			int actualupgradeBuy = car.getInt(NUUB);
			if (actualupgradeBuy != nbUpgradeBuy) {
				CheckReport checkReport = new CheckReport();
				checkReport.setError(ErrorType.WRONG_NUUB);
				checkReport.setMessage(
						car.getString("crdb") + ": Actualement=" + actualupgradeBuy + ", attendu=" + nbUpgradeBuy);
				reports.add(checkReport);
			}

			pos++;
		}

		return reports;
	}

	@Override
	public List<CheckReport> correct(String path) throws NsbException {
		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);

		JsonArrayBuilder newCaowObject = Json.createArrayBuilder();

		int pos = 0;
		while (pos < caowObject.size()) {

			JsonObject car = caowObject.getJsonObject(pos);
			int nbUpgradeBuy = new CarUpgradeCalculator().compute(car);

			int actualupgradeBuy = car.getInt(NUUB);
			if (actualupgradeBuy != nbUpgradeBuy) {
				JsonObjectBuilder newCar = Json.createObjectBuilder(car);
				newCar.add(NUUB, nbUpgradeBuy);
				newCaowObject.add(newCar);
			} else {
				newCaowObject.add(car);
			}
			pos++;
		}

		JsonObjectBuilder newJsonObject = Json.createObjectBuilder(nsbObject);
		newJsonObject.add(CAOW, newCaowObject);
		fileWriter.write(nsb, newJsonObject);

		return check(path);
	}

}
