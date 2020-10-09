package com.naturalmotion.csr_api.service.http;

import java.io.*;
import java.nio.file.Files;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.naturalmotion.csr_api.Configuration;

public class HttpFileReader {

    public JsonObject readJson(String path) throws HttpCsrExcetion {
        JsonObject jsonObject = null;
        String read = read(path);
        try (JsonReader reader = Json.createReader(new StringReader(read));) {
            jsonObject = reader.readObject();
        }
        return jsonObject;
    }

    public String read(String path) throws HttpCsrExcetion {
        try {
            Configuration conf = new Configuration();
            String url = conf.getString("csr.collection.url");
            File file = new File(url + path);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new HttpCsrExcetion(path, e);
        }
    }
}
