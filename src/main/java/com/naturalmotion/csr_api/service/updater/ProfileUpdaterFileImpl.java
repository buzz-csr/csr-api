package com.naturalmotion.csr_api.service.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import com.naturalmotion.csr_api.Configuration;
import com.naturalmotion.csr_api.api.Resource;
import com.naturalmotion.csr_api.api.ResourceType;
import com.naturalmotion.csr_api.service.reader.ProfileReader;
import com.naturalmotion.csr_api.service.reader.ProfileReaderFileImpl;
import com.naturalmotion.csr_api.service.reader.ReaderException;

public class ProfileUpdaterFileImpl implements ProfileUpdater {

    private String path;
    private ProfileReader profileReader;

    public ProfileUpdaterFileImpl(String path) {
        this.path = path;
        profileReader = new ProfileReaderFileImpl(path);
    }

    @Override
    public void updateResource(ResourceType type, BigDecimal expected) throws UpdaterException {
        File editedDirectory = new File(path + "/Edited");
        if (!editedDirectory.exists()) {
            throw new UpdaterException(path + " doesn't exist");
        }
        File scb = new File(editedDirectory.getAbsolutePath() + "/scb.json");
        if (!scb.exists()) {
            throw new UpdaterException("Missing scb file into Edited folder");
        }
        File nsb = new File(editedDirectory.getAbsolutePath() + "/nsb.json");
        if (!scb.exists()) {
            throw new UpdaterException("Missing nsb file into Edited folder");
        }

        BigDecimal newValue = getValue(type, expected);

        editFile(type, newValue, scb, "scb");
        editFile(type, newValue, nsb, "nsb");
    }

    private BigDecimal getValue(ResourceType type, BigDecimal expected) throws UpdaterException {
        BigDecimal newValue = null;
        try {
            List<Resource> resources = profileReader.getResources();
            Resource actual = resources.stream().filter(x -> x.getType().equals(type)).findFirst().orElse(null);
            if (actual != null) {
                newValue = getNewValue(expected, actual);
            }
        } catch (ReaderException e) {
            throw new UpdaterException("Error reading profile", e);
        }
        return newValue;
    }

    private void editFile(ResourceType type, BigDecimal value, File file, String name) throws UpdaterException {
        JsonObject newJsonObject = getNewJson(type, value, file, name);

        if (newJsonObject != null) {
            File temp = writeNewJsonToTemp(file, name, newJsonObject);
            deleteOriginalFile(file, temp);
            renameTempFile(file, temp);
        }
    }

    private void renameTempFile(File file, File temp) throws UpdaterException {
        try {
            Files.copy(temp.toPath(), file.toPath());
            Files.deleteIfExists(temp.toPath());
        } catch (IOException e) {
            try {
                Files.deleteIfExists(temp.toPath());
            } catch (IOException e1) {
                throw new UpdaterException("Error deleting file " + temp.getName(), e1);
            }
            throw new UpdaterException("Error copying file " + file.getName(), e);
        }
    }

    private void deleteOriginalFile(File file, File temp) throws UpdaterException {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            try {
                Files.deleteIfExists(temp.toPath());
                throw new UpdaterException("Error deleting file " + file.getName(), e);
            } catch (IOException e1) {
                throw new UpdaterException("Error deleting file " + temp.getName(), e1);
            }
        }
    }

    private File writeNewJsonToTemp(File file, String name, JsonObject newjsonObject) throws UpdaterException {
        String temp = file.getPath() + ".temp";
        try (FileWriter writer = new FileWriter(temp);) {
            writer.write(newjsonObject.toString());
        } catch (IOException e) {
            throw new UpdaterException("Error updating file " + name, e);
        }
        return new File(temp);
    }

    private JsonObject getNewJson(ResourceType type, BigDecimal value, File file, String name)
            throws UpdaterException {
        JsonObject newjsonObject = null;
        try (InputStream fis = new FileInputStream(file); JsonReader reader = Json.createReader(fis);) {
            Configuration conf = new Configuration();
            if (value != null) {
                JsonObject json = reader.readObject();

                JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(null);
                JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
                for (String key : json.keySet()) {
                    jsonObjectBuilder.add(key, json.get(key));
                }

                jsonObjectBuilder.add(getLabel(type, name, conf), value); // update spent value
                newjsonObject = jsonObjectBuilder.build();
            }
        } catch (IOException e) {
            throw new UpdaterException("Error updating profile", e);
        }
        return newjsonObject;
    }

    private String getLabel(ResourceType type, String name, Configuration conf) {
        return conf.getString(type.name() + "." + name + ".spent");
    }

    private BigDecimal getNewValue(BigDecimal expected, Resource actual) {
        BigDecimal newValue = null;
        if (expected.compareTo(actual.getSpent()) > 0) {
            newValue = BigDecimal.ZERO;
        } else {
            newValue = actual.getSpent().subtract(expected);
        }
        return newValue;
    }

}
