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

    private static final String CAOW = "caow";

    private static final String CRDB = "crdb";

    private static final String UNID = "unid";

    private final String path;

    public CarServiceFileImpl(String path) {
        this.path = path;
    }

    @Override
    public JsonObject replace(int idToReplace, String newCarPath) throws CarException {
        JsonObject newCarFull = createNewCarFull(idToReplace, newCarPath);

        File nsb = getNsbFile();
        JsonObject nsbObject = readJsonObject(nsb);
        JsonArray caowObject = nsbObject.getJsonArray(CAOW);
        JsonArrayBuilder newCaow = createNewCaow(idToReplace, caowObject, newCarFull);
        JsonObjectBuilder newNsb = copyJsonObject(nsbObject);
        newNsb.add(CAOW, newCaow);

        writeNsb(nsb, newNsb);
        return newCarFull;
    }

    @Override
    public JsonObject full(int id) throws CarException {
        JsonObject carFull = null;

        File nsb = getNsbFile();
        JsonObject nsbObject = readJsonObject(nsb);
        JsonArray caowObject = nsbObject.getJsonArray(CAOW);
        JsonObject jsonCarToMax = findCarFromId(id, caowObject);

        if (jsonCarToMax != null) {
            String name = jsonCarToMax.getString(CRDB);
            JsonObject jsonCarFull = getJsonCarFull(name);

            if (jsonCarFull != null) {
                JsonObject newCarFull = mergeFusion(jsonCarToMax, jsonCarFull, id);

                JsonArrayBuilder newCaow = createNewCaow(id, caowObject, newCarFull);
                JsonObjectBuilder newNsb = copyJsonObject(nsbObject);
                newNsb.add(CAOW, newCaow);
                writeNsb(nsb, newNsb);

                carFull = newCarFull.asJsonObject();
            } else {
                throw new CarException("Car name " + name + " not found into nsb full");
            }
        } else {
            throw new CarException("Car unid " + id + " not found into nsb");
        }
        return carFull;
    }

    private JsonObject getJsonCarFull(String carName) throws CarException {
        JsonObject nsbFull = readNsbFull();
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

    private JsonObject readNsbFull() throws CarException {
        JsonObject json = null;

        try (InputStream fis = this.getClass().getClassLoader().getResourceAsStream("nsb.full.txt");
                JsonReader reader = Json.createReader(fis);) {
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

        JsonArray caowObject = nsbObject.getJsonArray(CAOW);
        JsonArrayBuilder cgpiNew = getCgpiNew(nsbObject, caowObject);

        int carId = nsbObject.getInt("ncui");
        JsonObject newCarFull = createNewCarFull(carId, newCarPath);

        JsonArrayBuilder caow = Json.createArrayBuilder(caowObject);
        caow.add(newCarFull);

        JsonObjectBuilder copyNsbObject = copyJsonObject(nsbObject);
        copyNsbObject.add(CAOW, caow);
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
            JsonObject carFull = getCarFull(carJson.getString(CRDB));
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
            objectBuilder.add(entry.getKey(), entry.getValue());
        }
        objectBuilder.add(UNID, carId);
        JsonArrayBuilder upst = Json.createArrayBuilder(carFull.getJsonArray("upst"));
        objectBuilder.add("upst", upst);
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
        return objectBuilder.build();
    }

    private JsonObject getCarFull(String searchId) throws IOException {
        JsonObject carFull = null;
        try (InputStream fis = this.getClass().getClassLoader().getResourceAsStream("nsb.full.txt");
                JsonReader reader = Json.createReader(fis);) {
            JsonObject json = reader.readObject();
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
        }
        return carFull;
    }
}
