package com.naturalmotion.csr_api.service.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JsonBuilder {

    public JsonObject readJsonObject(File file) throws NsbException {
        JsonObject nsbObject = null;
        try (JsonReader reader = Json.createReader(new FileInputStream(file));) {
            nsbObject = reader.readObject();
        } catch (FileNotFoundException e) {
            throw new NsbException(e);
        }
        return nsbObject;
    }
}
