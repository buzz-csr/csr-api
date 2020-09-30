package com.naturalmotion.csr_api.service.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
        URI uri = null;
        StringBuilder builder = new StringBuilder();
        try {
            Configuration conf = new Configuration();
            String url = conf.getString("csr.collection.url");
            URL target = new URL(url + path);
            uri = new URI(target.getProtocol(), target.getUserInfo(), target.getHost(), target.getPort(),
                    target.getPath(), target.getQuery(), target.getRef());

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.109.225.39", 8080));

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(uri.toURL().openConnection(proxy).getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }
        } catch (IOException
                | URISyntaxException e) {
            throw new HttpCsrExcetion(uri, e);
        }
        return builder.toString();
    }
}
