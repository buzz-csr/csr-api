package com.naturalmotion.csr_api.service.car;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import com.naturalmotion.csr_api.service.car.comparator.BrandComparator;
import com.naturalmotion.csr_api.service.car.comparator.ComparatorParameter;
import com.naturalmotion.csr_api.service.car.comparator.TierComparator;
import com.naturalmotion.csr_api.service.http.HttpCsrExcetion;
import com.naturalmotion.csr_api.service.http.HttpFileReader;
import com.naturalmotion.csr_api.service.io.JsonBuilder;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;
import com.naturalmotion.csr_api.service.io.ProfileFileWriter;

public class CarServiceFileImpl implements CarService {

	private static final String UPST = "upst";

	private static final String CMLV = "cmlv";

	private static final String ELCL = "elcl";

	private static final String CAOW = "caow";

	private static final String CRDB = "crdb";

	private static final String UNID = "unid";

	private final String path;

	private ProfileFileWriter fileWriter = new ProfileFileWriter();

	private NsbReader nsbReader = new NsbReader();

	private JsonBuilder jsonBuilder = new JsonBuilder();

	private FusionCalculator fusionCalculator = new FusionCalculator();

	public CarServiceFileImpl(String path) {
		this.path = path;
	}

	@Override
	public JsonObject replace(int idToReplace, String newCarPath) throws CarException, NsbException {
		JsonObject newCarFull = createNewCarFull(idToReplace, newCarPath);

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		JsonArrayBuilder newCaow = createNewCaow(idToReplace, caowObject, newCarFull);
		JsonObjectBuilder newNsb = Json.createObjectBuilder(nsbObject);
		newNsb.add(CAOW, newCaow);
		newNsb.add("cidc", new CidcUpdater().update(newCarFull.getString("crdb"), nsbObject));
		newNsb.add("cid2", new Cid2Updater().update(newCarFull.getString("crdb"), nsbObject));

		fileWriter.write(nsb, newNsb);
		return newCarFull;
	}

	@Override
	public JsonObject full(int id) throws CarException, NsbException {
		JsonObject carFull = null;

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		JsonObject jsonCarToMax = findCarFromId(id, caowObject);

		if (jsonCarToMax != null) {
			String name = jsonCarToMax.getString(CRDB);
			JsonObject jsonCarFull = getJsonCarFull(name);

			if (jsonCarFull != null) {
				JsonObject newCarFull = mergeFusion(jsonCarToMax, jsonCarFull, id);

				JsonArrayBuilder newCaow = createNewCaow(id, caowObject, newCarFull);
				JsonObjectBuilder newNsb = Json.createObjectBuilder(nsbObject);
				newNsb.add(CAOW, newCaow);
				fileWriter.write(nsb, newNsb);

				carFull = newCarFull.asJsonObject();
			} else {
				throw new CarException("Car name " + name + " not found into nsb full");
			}
		} else {
			throw new CarException("Car unid " + id + " not found into nsb");
		}
		return carFull;
	}

	private JsonObject getJsonCarFull(String carName) throws NsbException {
		JsonObject nsbFull = nsbReader.getNsbFull();
		JsonArray carList = nsbFull.getJsonArray(CAOW);
		return findCarFromName(carName, carList);
	}

	private JsonArrayBuilder createNewCaow(int id, JsonArray caowObject, JsonObject newCarFull) {
		JsonArrayBuilder newCaow = Json.createArrayBuilder();
		int pos = 0;
		while (pos < caowObject.size()) {
			JsonObject carTemp = caowObject.getJsonObject(pos);
			int idTemp = carTemp.getInt(UNID);
			if (id != idTemp) {
				newCaow.add(carTemp);
			} else {
				newCaow.add(newCarFull);
			}
			pos++;
		}
		return newCaow;
	}

	private JsonObject findCarFromId(int id, JsonArray carList) {
		JsonObject jsonCar = null;
		int pos = 0;
		while (jsonCar == null && pos < carList.size()) {
			JsonObject carTemp = carList.getJsonObject(pos);
			int idTemp = carTemp.getInt(UNID);
			if (id == idTemp) {
				jsonCar = carTemp;
			}
			pos++;
		}
		return jsonCar;
	}

	private JsonObject findCarFromName(String carName, JsonArray carList) {
		JsonObject jsonCar = null;
		int pos = 0;
		while (jsonCar == null && pos < carList.size()) {
			JsonObject carTemp = carList.getJsonObject(pos);
			String idTemp = carTemp.getString(CRDB);
			if (carName.equals(idTemp)) {
				jsonCar = carTemp;
			}
			pos++;
		}
		return jsonCar;
	}

	@Override
	public JsonObject add(String newCarPath) throws CarException, NsbException {
		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);

		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		JsonArrayBuilder cgpiNew = getCgpiNew(nsbObject, caowObject);

		int carId = nsbObject.getInt("ncui");
		JsonObject newCarFull = createNewCarFull(carId, newCarPath);

		JsonArrayBuilder caow = Json.createArrayBuilder(caowObject);
		caow.add(newCarFull);

		JsonObjectBuilder copyNsbObject = Json.createObjectBuilder(nsbObject);
		copyNsbObject.add(CAOW, caow);
		copyNsbObject.add("cgpi", cgpiNew);
		copyNsbObject.add("ncui", ++carId);
		copyNsbObject.add("cidc", new CidcUpdater().update(newCarFull.getString("crdb"), nsbObject));
		copyNsbObject.add("cid2", new Cid2Updater().update(newCarFull.getString("crdb"), nsbObject));

		fileWriter.write(nsb, copyNsbObject);
		return newCarFull;
	}

	private JsonObject createNewCarFull(int carId, String newCarPath) throws CarException {
		try {
			JsonObject carJson = new HttpFileReader().readJson(newCarPath);
			JsonObject carFull = getCarFull(carJson.getString(CRDB));
			return mergeFusion(carJson, carFull, carId);
		} catch (HttpCsrExcetion | NsbException e) {
			throw new CarException(e);
		}
	}

	private JsonArrayBuilder getCgpiNew(JsonObject nsbObject, JsonArray caowObject) {
		JsonArray cgpi = nsbObject.getJsonArray("cgpi");
		int size = caowObject.size();
		return new CgpiUpdater().update(cgpi, size);
	}

	private JsonObject mergeFusion(JsonObject carJson, JsonObject carFull, int carId) {
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder(carJson);
		objectBuilder.add(UNID, carId);

		JsonArrayBuilder upst = Json.createArrayBuilder(carFull.getJsonArray(UPST));
		objectBuilder.add(UPST, upst);
		JsonArrayBuilder grsp = Json.createArrayBuilder(carFull.getJsonArray("grsp"));
		objectBuilder.add("grsp", grsp);
		objectBuilder.add("fidr", carFull.getJsonNumber("fidr"));
		objectBuilder.add("nlpr", carFull.getJsonNumber("nlpr"));
		objectBuilder.add("tafr", carFull.getJsonNumber("tafr"));
		objectBuilder.add("titp", carFull.getJsonNumber("titp"));
		objectBuilder.add("trld", carFull.getJsonNumber("trld"));
		objectBuilder.add("fidr", carFull.getJsonNumber("fidr"));
		objectBuilder.add("tssp", carFull.getJsonNumber("tssp"));
		objectBuilder.add("tssr", carFull.getJsonNumber("tssr"));
		objectBuilder.add("ttup", carFull.getJsonNumber("ttup"));
		objectBuilder.add("ttpp", carFull.getJsonNumber("ttpp"));
		objectBuilder.add("cepi", carFull.getJsonNumber("cepi"));
		objectBuilder.add("nuub", new CarUpgradeCalculator().compute(carFull));

		return objectBuilder.build();
	}

	private JsonObject getCarFull(String searchId) throws NsbException {
		JsonObject carFull = null;
		JsonObject json = nsbReader.getNsbFull();
		JsonArray carList = json.getJsonArray(CAOW);
		int pos = 0;
		while (carFull == null && pos < carList.size()) {
			JsonObject carTemp = carList.getJsonObject(pos);
			String id = carTemp.getString(CRDB);

			if (id.equals(searchId)) {
				carFull = carTemp;
			}
			pos++;
		}
		return carFull;
	}

	@Override
	public JsonObject elite(int id) throws CarException, NsbException {
		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		JsonObject jsonCarToUpdate = findCarFromId(id, caowObject);

		JsonObject newCar = null;
		if (jsonCarToUpdate != null) {
			JsonObjectBuilder carBuilder = Json.createObjectBuilder(jsonCarToUpdate);
			carBuilder.add(ELCL, findEliteLicenseNumber(caowObject));

			newCar = carBuilder.build();
			JsonArrayBuilder newCaow = createNewCaow(id, caowObject, newCar);
			JsonObjectBuilder newNsb = Json.createObjectBuilder(nsbObject);
			newNsb.add(CAOW, newCaow);
			fileWriter.write(nsb, newNsb);
		}

		return newCar;
	}

	private Integer findEliteLicenseNumber(JsonArray caowObject) throws CarException {
		Map<Integer, Integer> license = new HashMap<>();
		license.put(1, 0);
		license.put(2, 0);
		license.put(3, 0);
		for (int index = 0; index < caowObject.size(); index++) {
			JsonObject jsonCar = caowObject.getJsonObject(index);
			int elcl = jsonCar.getInt(ELCL);
			if (elcl > 0) {
				Integer licenceNumber = license.get(elcl);
				license.remove(elcl);
				license.put(elcl, licenceNumber + 1);
			}
		}
		Integer licenseNumber = license.keySet().stream().filter(x -> license.get(x) < 5).findFirst().orElse(null);
		if (licenseNumber == null) {
			throw new CarException("No more elite license available");
		}
		return licenseNumber;
	}

	@Override
	public JsonArray removeEliteLevel() throws NsbException {
		JsonArrayBuilder eliteList = Json.createArrayBuilder();

		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		JsonArrayBuilder caowBuilder = Json.createArrayBuilder();

		for (int index = 0; index < caowObject.size(); index++) {

			JsonObject jsonCar = caowObject.getJsonObject(index);
			if (jsonCar.getInt(CMLV) > 0) {
				eliteList.add(createEliteCarResult(jsonCar, index));

				JsonObject carFull = getCarFull(jsonCar.getString(CRDB));
				JsonObjectBuilder newJsonCar = Json.createObjectBuilder(jsonCar);
				newJsonCar.add(CMLV, 0);
				if (carFull != null) {
					int actualFusionNb = fusionCalculator.getFusionNumber(jsonCar);
					int fullFusionNb = fusionCalculator.getFusionNumber(carFull);
					if (actualFusionNb > fullFusionNb) {
						caowBuilder.add(mergeFusion(newJsonCar.build(), carFull, jsonCar.getInt(UNID)));
					}
				} else {
					caowBuilder.add(newJsonCar);
				}
			} else {
				caowBuilder.add(jsonCar);
			}
		}
		JsonObjectBuilder nsbBuilder = Json.createObjectBuilder(nsbObject);
		nsbBuilder.add(CAOW, caowBuilder);

		JsonObject result = nsbBuilder.build();
		fileWriter.write(nsb, result);
		return eliteList.build();
	}

	private JsonObject createEliteCarResult(JsonObject jsonCar, int index) {
		JsonObjectBuilder eliteCar = Json.createObjectBuilder();
		eliteCar.add("name", jsonCar.getString(CRDB));
		eliteCar.add("level", jsonCar.getInt(CMLV));
		eliteCar.add("garage", Math.floor(index / 6 + 1));
		return eliteCar.build();
	}

	@Override
	public JsonObject sort(ComparatorParameter param, boolean eliteFirst) throws NsbException {
		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray cgpiObject = nsbObject.getJsonArray("cgpi");
		List<Integer> caowList = new ArrayList<>();
		for (int i = 0; i < cgpiObject.size(); i++) {
			caowList.add(cgpiObject.getInt(i));
		}

		List<String> eliteCars = getEliteCars(eliteFirst);

		sort(param, nsbObject, caowList, eliteCars);

		JsonArrayBuilder cgpiBuilder = Json.createArrayBuilder();
		caowList.forEach(x -> cgpiBuilder.add(x));

		JsonObjectBuilder nsbBuilder = Json.createObjectBuilder(nsbObject);
		nsbBuilder.add("cgpi", cgpiBuilder);

		JsonObject result = nsbBuilder.build();
		fileWriter.write(nsb, result);
		return result;
	}

	private void sort(ComparatorParameter param, JsonObject nsbObject, List<Integer> caowList, List<String> eliteCars) {
		if (ComparatorParameter.BRAND.equals(param)) {
			caowList.sort(new BrandComparator(nsbObject.getJsonArray(CAOW), eliteCars));
		} else {
			caowList.sort(new TierComparator(nsbObject.getJsonArray(CAOW), eliteCars));
		}
	}

	private List<String> getEliteCars(boolean eliteFirst) throws NsbException {
		List<String> eliteCars;
		if (!eliteFirst) {
			eliteCars = new ArrayList<>();
		} else {
			eliteCars = getEliteCars();
		}
		return eliteCars;
	}

	@Override
	public List<String> getEliteCars() throws NsbException {
		List<String> carsList = new ArrayList<>();

		JsonObject jsonFull = nsbReader.getNsbFull();
		JsonObject eliteGroupes = jsonFull.getJsonObject("ccac");
		Set<Entry<String, JsonValue>> entrySet = eliteGroupes.entrySet();
		for (Entry<String, JsonValue> entry : entrySet) {
			JsonArray cars = entry.getValue().asJsonArray();
			cars.stream().forEach(x -> carsList.add(((JsonString) x).getString()));
		}
		return carsList;
	}

	@Override
	public JsonObject removeElite(int id) throws CarException, NsbException {
		File nsb = nsbReader.getNsbFile(path);
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray caowObject = nsbObject.getJsonArray(CAOW);
		JsonObject jsonCarToUpdate = findCarFromId(id, caowObject);
		JsonObject newCar = null;
		if (jsonCarToUpdate != null) {
			JsonObjectBuilder carBuilder = Json.createObjectBuilder(jsonCarToUpdate);
			carBuilder.add(ELCL, 0);

			newCar = carBuilder.build();
			JsonArrayBuilder newCaow = createNewCaow(id, caowObject, newCar);
			JsonObjectBuilder newNsb = Json.createObjectBuilder(nsbObject);
			newNsb.add(CAOW, newCaow);
			fileWriter.write(nsb, newNsb);
		}

		return newCar;
	}
}
