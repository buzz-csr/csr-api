package com.naturalmotion.csr_api.service.car;

import java.io.File;
import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naturalmotion.csr_api.service.NsbEditedTest;
import com.naturalmotion.csr_api.service.io.JsonBuilder;
import com.naturalmotion.csr_api.service.io.NsbException;
import com.naturalmotion.csr_api.service.io.NsbReader;

public class CarUpgradeCalculatorTest {

	private static final int MERCO_45AMG = 17;

	private static final int FERRARI_458SEPECIAL = 32;

	private static final int VIPER_ACR_UNID = 98;

	private NsbReader nsbReader = new NsbReader();

	private JsonBuilder jsonBuilder = new JsonBuilder();

	private CarUpgradeCalculator calculator = new CarUpgradeCalculator();

	@Before
	public void setup() throws IOException {
		new NsbEditedTest().backup();
	}

	@After
	public void after() throws IOException {
		new NsbEditedTest().restore();
	}

	@Test
	public void testCompute() throws Exception {
		testCarNuub(VIPER_ACR_UNID, 27);
		testCarNuub(FERRARI_458SEPECIAL, 0);
		testCarNuub(MERCO_45AMG, 35);
	}

	private void testCarNuub(int unid, int expected) throws NsbException {
		JsonObject car = getCar(unid);
		Assertions.assertThat(calculator.compute(car)).isEqualTo(expected);
	}

	private JsonObject getCar(int unid) throws NsbException {
		File nsb = nsbReader.getNsbFile("target");
		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);
		JsonArray cars = nsbObject.getJsonArray("caow");
		JsonObject car = null;
		int pos = 0;
		while (car == null && pos < cars.size()) {
			JsonObject actual = cars.getJsonObject(pos);
			if (actual.getInt("unid") == unid) {
				car = actual;
			}
			pos++;
		}
		return car;
	}

}
