package com.naturalmotion.csr_api.service.io;

import com.naturalmotion.csr_api.service.car.CarException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;

public class NsbReader {

    public File getNsbFile(String path) throws NsbException {
        File editedDirectory = new File(path + "/Edited");
        if (!editedDirectory.exists()) {
            throw new NsbException(path + " doesn't exist");
        }
        File nsb = new File(editedDirectory.getAbsolutePath() + "/nsb.json");
        if (!nsb.exists()) {
            throw new NsbException("Missing nsb file into Edited folder");
        }
        return nsb;
    }

    public JsonObject readJsonObject(File nsb) throws NsbException {
        JsonObject nsbObject = null;
        try (JsonReader reader = Json.createReader(new FileInputStream(nsb));) {
            nsbObject = reader.readObject();
        } catch (FileNotFoundException e) {
            throw new NsbException(e);
        }
        return nsbObject;
    }

    public JsonObject getNsbFull() throws NsbException {
        JsonObject json = null;

        try (InputStream fis = this.getClass().getClassLoader().getResourceAsStream("nsb.full.txt");
             JsonReader reader = Json.createReader(fis);) {
            json = reader.readObject();
        } catch (IOException e) {
            throw new NsbException(e);
        }
        return json;
    }

}
