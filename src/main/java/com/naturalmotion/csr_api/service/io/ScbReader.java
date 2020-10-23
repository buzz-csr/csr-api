package com.naturalmotion.csr_api.service.io;

import java.io.File;

public class ScbReader {

    public File getScbFile(String path) throws NsbException {
        File editedDirectory = new File(path + "/Edited");
        if (!editedDirectory.exists()) {
            throw new NsbException(path + " doesn't exist");
        }
        File scb = new File(editedDirectory.getAbsolutePath() + "/scb.json");
        if (!scb.exists()) {
            throw new NsbException("Missing scb file into Edited folder");
        }
        return scb;
    }

}
