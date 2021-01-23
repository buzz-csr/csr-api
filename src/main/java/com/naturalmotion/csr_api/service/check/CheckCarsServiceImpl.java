package com.naturalmotion.csr_api.service.check;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.naturalmotion.csr_api.service.io.JsonBuilder;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;
import com.naturalmotion.csr_api.service.io.ProfileFileWriter;

public class CheckCarsServiceImpl implements CheckCarsService {

	private static final String CAOW = "caow";

	private NsbReader nsbReader = new NsbReader();

	private JsonBuilder jsonBuilder = new JsonBuilder();

	private ProfileFileWriter fileWriter = new ProfileFileWriter();

	private NuubChecker nuubChecker = new NuubChecker();

	private FusionChecker fusionCheck;

	public CheckCarsServiceImpl() throws NsbException {
		fusionCheck = new FusionChecker();
	}

	@Override
	public List<CheckReport> check(String path) throws NsbException {
		List<CheckReport> reports = new ArrayList<>();

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		int pos = 0;
		while (pos < caowObject.size()) {

			JsonObject car = caowObject.getJsonObject(pos);

			CheckReport nuubReport = nuubChecker.checkCar(car);
			if (nuubReport != null) {
				reports.add(nuubReport);
			}
			CheckReport fusionReport = fusionCheck.checkCar(car);
			if (fusionReport != null) {
				reports.add(fusionReport);
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
			JsonObject newCar = nuubChecker.correct(car);
			newCaowObject.add(newCar);

			pos++;
		}

		JsonObjectBuilder newJsonObject = Json.createObjectBuilder(nsbObject);
		newJsonObject.add(CAOW, newCaowObject);
		fileWriter.write(nsb, newJsonObject);

		return check(path);
	}

}
