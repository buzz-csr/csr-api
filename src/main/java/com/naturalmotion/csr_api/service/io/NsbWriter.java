package com.naturalmotion.csr_api.service.io;

import com.naturalmotion.csr_api.service.car.CarException;

import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NsbWriter {

    public void writeNsb(File nsb, JsonObjectBuilder copyNsbObject) throws NsbException {
        try (FileWriter fileWriter = new FileWriter(nsb);) {
            fileWriter.write(copyNsbObject.build().toString());
        } catch (IOException e) {
            throw new NsbException(e);
        }
    }
}
