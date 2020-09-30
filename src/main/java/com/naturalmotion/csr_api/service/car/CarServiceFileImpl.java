package com.naturalmotion.csr_api.service.car;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.naturalmotion.csr_api.service.http.HttpCsrExcetion;
import com.naturalmotion.csr_api.service.http.HttpFileReader;

public class CarServiceFileImpl implements CarService {

    private final String path;

    public CarServiceFileImpl(String path) {
        this.path = path;
    }

    @Override
    public void replace(String idToReplace, String newCarPath) {

    }

    @Override
    public void full(String id) throws CarException {
        JsonObject jsonCarFull = getJsonCarFull(id);

        File nsb = getNsbFile();
        JsonObject nsbObject = readJsonObject(nsb);
        JsonArray caowObject = nsbObject.getJsonArray("caow");
        JsonObject jsonCarToMax = findCar(id, caowObject);
        JsonObject newCarFull = mergeFusion(jsonCarToMax, jsonCarFull, jsonCarToMax.getInt("unid"));

        JsonArrayBuilder newCaow = createNewCaow(id, caowObject, newCarFull);
        JsonObjectBuilder newNsb = copyJsonObject(nsbObject);
        newNsb.add("caow", newCaow);

        writeNsb(nsb, newNsb);
    }

    public JsonObject getJsonCarFull(String id) throws CarException {
        JsonObject nsbFull = readNsbFull();
        JsonArray carList = nsbFull.getJsonArray("caow");
        return findCar(id, carList);
    }

    public JsonArrayBuilder createNewCaow(String id, JsonArray caowObject, JsonObject newCarFull) {
        JsonArrayBuilder newCaow = Json.createArrayBuilder();
        int pos = 0;
        while (pos < caowObject.size()) {
            JsonObject carTemp = caowObject.getJsonObject(pos);
            String idTemp = carTemp.getString("crdb");
            if (!id.equals(idTemp)) {
                newCaow.add(carTemp);
            } else {
                newCaow.add(newCarFull);
            }
            pos++;
        }
        return newCaow;
    }

    public JsonObject findCar(String id, JsonArray carList) {
        JsonObject jsonCar = null;
        int pos = 0;
        while (jsonCar == null && pos < carList.size()) {
            JsonObject carTemp = carList.getJsonObject(pos);
            String idTemp = carTemp.getString("crdb");
            if (id.equals(idTemp)) {
                jsonCar = carTemp;
            }
            pos++;
        }
        return jsonCar;
    }

    public JsonObject readNsbFull() throws CarException {
        JsonObject json = null;
        File nsbFull = new File("src/main/resources/nsb.full.txt");
        try (InputStream fis = new FileInputStream(nsbFull); JsonReader reader = Json.createReader(fis);) {
            json = reader.readObject();
        } catch (IOException e) {
            throw new CarException(e);
        }
        return json;
    }

    @Override
    public void add(String newCarPath) throws CarException {
        File nsb = getNsbFile();
        JsonObject nsbObject = readJsonObject(nsb);

        JsonArray caowObject = nsbObject.getJsonArray("caow");
        JsonArrayBuilder cgpiNew = getCgpiNew(nsbObject, caowObject);

        int carId = nsbObject.getInt("ncui");
        JsonObject newCarFull = createNewCarFull(carId, newCarPath);

        JsonArrayBuilder caow = Json.createArrayBuilder(caowObject);
        caow.add(newCarFull);

        JsonObjectBuilder copyNsbObject = copyJsonObject(nsbObject);
        copyNsbObject.add("caow", caow);
        copyNsbObject.add("cgpi", cgpiNew);
        copyNsbObject.add("ncui", ++carId);

        writeNsb(nsb, copyNsbObject);
    }

    private void writeNsb(File nsb, JsonObjectBuilder copyNsbObject) throws CarException {
        try (FileWriter fileWriter = new FileWriter(nsb);) {
            fileWriter.write(copyNsbObject.build().toString());
        } catch (IOException e) {
            throw new CarException(e);
        }
    }

    private JsonObject createNewCarFull(int carId, String newCarPath) throws CarException {
        try {
            JsonObject carJson = new HttpFileReader().readJson(newCarPath);
            JsonObject carFull = getCarFull(carJson.getString("crdb"));
            return mergeFusion(carJson, carFull, carId);
        } catch (HttpCsrExcetion
                | IOException e) {
            throw new CarException(e);
        }
    }

    private File getNsbFile() throws CarException {
        File editedDirectory = new File(path + "/Edited");
        if (!editedDirectory.exists()) {
            throw new CarException(path + " doesn't exist");
        }
        File nsb = new File(editedDirectory.getAbsolutePath() + "/nsb.json");
        if (!nsb.exists()) {
            throw new CarException("Missing nsb file into Edited folder");
        }
        return nsb;
    }

    private JsonObjectBuilder copyJsonObject(JsonObject nsbObject) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        for (Entry<String, JsonValue> entry : nsbObject.entrySet()) {
            objectBuilder.add(entry.getKey(), entry.getValue());
        }
        return objectBuilder;
    }

    private JsonArrayBuilder getCgpiNew(JsonObject nsbObject, JsonArray caowObject) {
        JsonArray cgpi = nsbObject.getJsonArray("cgpi");
        int size = caowObject.size();
        return new CgpiUpdater().update(cgpi, size);
    }

    private JsonObject readJsonObject(File nsb) throws CarException {
        JsonObject nsbObject = null;
        try (JsonReader reader = Json.createReader(new FileInputStream(nsb));) {
            nsbObject = reader.readObject();
        } catch (FileNotFoundException e) {
            throw new CarException(e);
        }
        return nsbObject;
    }

    private JsonObject mergeFusion(JsonObject carJson, JsonObject carFull, int carId) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        for (Entry<String, JsonValue> entry : carJson.entrySet()) {
            if ("unid".equals(entry.getKey())) {
                objectBuilder.add("unid", carId);
            } else {
                objectBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        JsonArrayBuilder upst = Json.createArrayBuilder(carFull.getJsonArray("upst"));
        objectBuilder.add("upst", upst);
        return objectBuilder.build();
    }

    private JsonObject getCarFull(String searchId) throws IOException {
        JsonObject carFull = null;
        File nsbFull = new File("src/main/resources/nsb.full.txt");
        try (InputStream fis = new FileInputStream(nsbFull); JsonReader reader = Json.createReader(fis);) {
            JsonObject json = reader.readObject();
            JsonArray carList = json.getJsonArray("caow");
            int pos = 0;
            while (carFull == null && pos < carList.size()) {
                JsonObject carTemp = carList.getJsonObject(pos);
                String id = carTemp.getString("crdb");

                if (id.equals(searchId)) {
                    carFull = carTemp;
                }
                pos++;
            }
        }
        return carFull;
    }
}
