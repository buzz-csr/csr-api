package com.naturalmotion.csr_api.service.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class ProfileFileWriter {

    public void write(File file, JsonObjectBuilder copyNsbObject) throws NsbException {
        write(file, copyNsbObject.build());
    }

    public void write(File file, JsonObject json) throws NsbException {
        try (FileWriter fileWriter = new FileWriter(file);) {
            fileWriter.write(json.toString());
        } catch (IOException e) {
            throw new NsbException(e);
        }
    }
}
