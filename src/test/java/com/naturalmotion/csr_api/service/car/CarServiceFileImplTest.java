package com.naturalmotion.csr_api.service.car;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naturalmotion.csr_api.service.NsbEditedTest;

public class CarServiceFileImplTest extends NsbEditedTest {

	private CarServiceFileImpl service = new CarServiceFileImpl("target");

	@Before
	public void setup() throws IOException {
		new NsbEditedTest().backup();
	}

	@After
	public void after() throws IOException {
		new NsbEditedTest().restore();
	}

	@Test
	public void testAdd() throws Exception, CarException {
		service.add("/01-Cars/Ford/F-150 SVT Raptor/Ruby Red.txt");

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
		service.full(197); // BMW_LBM4CoupeCrewRecycled_2014

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

				assertThat(asJsonObject.getInt("nuub")).isEqualTo(35);
			}
		});

	}

	@Test
	public void testReplace() throws Exception {
		service.replace(197, // BMW_LBM4CoupeCrewRecycled_2014
				"/01-Cars/Lamborghini/LB Murciélago LP670-4 SuperVeloce/Arancio Borealis.txt");

		JsonObject search = findCar(197);
		assertThat(search).isNotNull();
		assertThat(search.getString("crdb")).isEqualTo("Lamborghini_LBMurcielagoSVRewardRecycled_2009");
	}

	public JsonObject findCar(int carId) throws IOException, FileNotFoundException {
		JsonObject search = null;
		JsonObject nsbExpected = getNsb("target/Edited/nsb.json");
		JsonArray caow = nsbExpected.getJsonArray("caow");
		Iterator<JsonValue> iterator = caow.iterator();
		while (iterator.hasNext()) {
			JsonObject car = iterator.next().asJsonObject();
			if (car.getInt("unid") == carId) {
				search = car;
			}
		}
		return search;
	}

	@Test
	public void testGetEliteCars() throws Exception {
		List<String> eliteCars = service.getEliteCars();
		assertThat(eliteCars).hasSize(34);
	}

	@Test
	public void testListAll() throws Exception {
		List<String> listAll = service.listAll();
		assertThat(listAll.size()).isGreaterThan(0);
	}

}
