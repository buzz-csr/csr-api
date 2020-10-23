package com.naturalmotion.csr_api.service.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

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
