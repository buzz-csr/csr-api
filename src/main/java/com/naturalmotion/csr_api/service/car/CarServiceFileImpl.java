package com.naturalmotion.csr_api.service.car;

import com.naturalmotion.csr_api.service.http.HttpCsrExcetion;
import com.naturalmotion.csr_api.service.http.HttpFileReader;

import javax.json.*;
import java.io.*;

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
        File editedDirectory = new File(path + "/Edited");
        if (!editedDirectory.exists()) {
            throw new CarException(path + " doesn't exist");
        }
        File nsb = new File(editedDirectory.getAbsolutePath() + "/nsb.json");
        if (!nsb.exists()) {
            throw new CarException("Missing nsb file into Edited folder");
        }
        File nsbFull = new File("src/main/resources/nsb.full.txt");
        try (InputStream fis = new FileInputStream(nsbFull); JsonReader reader = Json.createReader(fis);) {
            JsonObject json = reader.readObject();
            JsonArray carList = json.getJsonArray("caow");

            JsonObject jsonCar = null;
            int pos = 0;
            while (jsonCar == null && pos < carList.size()) {
                JsonObject carTemp = carList.getJsonObject(pos);
                String idTemp = carTemp.getString("crdb");
                pos++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(String newCarPath) throws CarException {
        File editedDirectory = new File(path + "/Edited");
        if (!editedDirectory.exists()) {
            throw new CarException(path + " doesn't exist");
        }
        File nsb = new File(editedDirectory.getAbsolutePath() + "/nsb.json");
        if (!nsb.exists()) {
            throw new CarException("Missing nsb file into Edited folder");
        }
        JsonObject nsbObject = null;
        try (JsonReader reader = Json.createReader(new FileInputStream(nsb));) {
            nsbObject = reader.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String carId = nsbObject.getString("ncui");
        JsonArray caowObject = nsbObject.getJsonArray("caow");
        JsonArray cgpi = nsbObject.getJsonArray("cgpi");
        int size = caowObject.size();
        JsonArrayBuilder cgpiBuilder = new CgpiUpdater().update(cgpi, size);

        try {
            JsonObject carJson = new HttpFileReader().readJson(newCarPath);
            JsonObject carFull = getCarFull(carJson.getString("crdb"));
            JsonObject newCarFull = mergeFusion(carJson, carFull, carId);

            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            for (String key : nsbObject.keySet()) {
                objectBuilder.add(key, carJson.get(key));
            }
            JsonArrayBuilder caow = Json.createArrayBuilder(caowObject);
            caow.add(newCarFull);
            objectBuilder.add("caow", caow);
            objectBuilder.add("cgpi", cgpiBuilder);
            objectBuilder.add("ncui", String.valueOf(Integer.getInteger(carId) + 1));

        } catch (HttpCsrExcetion | IOException e) {
            throw new CarException(e);
        }

    }

    private JsonObject mergeFusion(JsonObject carJson, JsonObject carFull, String carId) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        for (String key : carJson.keySet()) {
            if ("unid".equals(key)) {
                objectBuilder.add("unid", carId);
            } else {
                objectBuilder.add(key, carJson.get(key));
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
