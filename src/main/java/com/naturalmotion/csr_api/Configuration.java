package com.naturalmotion.csr_api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private final Properties properties;

    public Configuration() throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("csr.properties");
        properties = new Properties();
        properties.load(resourceAsStream);
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }
}
