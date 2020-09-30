package com.naturalmotion.csr_api.service.car;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naturalmotion.csr_api.service.NsbEditedTest;

public class CarServiceFileImplTest extends NsbEditedTest {

    private CarServiceFileImpl service = new CarServiceFileImpl("target");

    @Before
    public void setup() throws IOException {
        new NsbEditedTest().backup();
        Authenticator authenticator = new Authenticator() {

            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication("jdesachy", "Juillet$032020".toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);

    }

    @After
    public void after() throws IOException {
        new NsbEditedTest().restore();
    }

    @Test
    public void testAdd() throws Exception, CarException {
        service.add("/V2.15.0/Lamborghini LB Murcielago LP670-4 SuperVeloce/Golden Star/Arancio Borealis.txt");

        JsonObject nsbExpected = getNsb("src/test/resources/Edited/nsb.json");
        assertThat(nsbExpected.getInt("ncui")).isEqualTo(1141);
        assertThat(nsbExpected.getJsonArray("caow").size()).isEqualTo(282);

        JsonObject nsbActual = getNsb("target/Edited/nsb.json");
        assertThat(nsbActual.getInt("ncui")).isEqualTo(1142);
        assertThat(nsbActual.getJsonArray("caow").size()).isEqualTo(283);
    }

    public JsonObject getNsb(String path) throws IOException, FileNotFoundException {
        JsonObject nsb = null;
        File actual = new File(path);
        try (InputStream fis = new FileInputStream(actual); JsonReader reader = Json.createReader(fis);) {
            nsb = reader.readObject();
        }
        return nsb;
    }

    @Test
    public void testFull() throws Exception, CarException {
        service.full("BMW_LBM4CoupeCrewRecycled_2014");

        JsonObject nsbExpected = getNsb("target/Edited/nsb.json");
        JsonArray caow = nsbExpected.getJsonArray("caow");
        caow.forEach(x -> {
            JsonObject asJsonObject = x.asJsonObject();
            if (asJsonObject.getInt("unid") == 197) {
                JsonArray upst = asJsonObject.getJsonArray("upst");
                JsonArray lvls = upst.get(0).asJsonObject().getJsonArray("lvls");
                assertThat(lvls.get(0).asJsonObject().getJsonArray("fsg").asJsonArray().toString())
                        .isEqualTo("[0,0,0,0,0]");
                assertThat(lvls.get(1).asJsonObject().getJsonArray("fsg").asJsonArray().toString())
                        .isEqualTo("[1,1,0,0,0]");
                assertThat(lvls.get(2).asJsonObject().getJsonArray("fsg").asJsonArray().toString())
                        .isEqualTo("[1,0,0,0,0]");
                assertThat(lvls.get(3).asJsonObject().getJsonArray("fsg").asJsonArray().toString())
                        .isEqualTo("[1,2,0,0,0]");
                assertThat(lvls.get(4).asJsonObject().getJsonArray("fsg").asJsonArray().toString())
                        .isEqualTo("[1,0,0,0,0]");
            }
        });

    }

}
