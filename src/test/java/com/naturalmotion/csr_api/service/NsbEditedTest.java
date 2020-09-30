package com.naturalmotion.csr_api.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class NsbEditedTest {

    public void backup() throws IOException {
        clean();
        File original = new File("src/test/resources/Edited");
        Path copied = Paths.get("target/Edited");
        Path originalPath = original.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        File[] listFiles = original.listFiles();
        for (File file : listFiles) {
            copied = Paths.get("target/Edited/" + file.getName());
            originalPath = file.toPath();
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void clean() throws IOException {
        File file = new File("target/Edited");
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            for (File toDelete : listFiles) {
                Files.deleteIfExists(toDelete.toPath());
            }
            Files.deleteIfExists(file.toPath());
        }
    }

    public void restore() throws IOException {
        clean();
    }
}
