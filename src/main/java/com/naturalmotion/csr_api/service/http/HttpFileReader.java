package com.naturalmotion.csr_api.service.http;

import com.naturalmotion.csr_api.Configuration;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
        StringBuilder builder = new StringBuilder();
        try {
            Configuration conf = new Configuration();
            String url = conf.getString("csr.collection.url");
            URL target = new URL(url + path);
            URI uri = new URI(target.getProtocol(), target.getUserInfo(), target.getHost(), target.getPort(), target.getPath(), target.getQuery(), target.getRef());
            BufferedReader in = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }
        } catch (IOException | URISyntaxException e) {
            throw new HttpCsrExcetion(e);
        }
        return builder.toString();
    }
}
