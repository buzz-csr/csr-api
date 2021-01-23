package com.naturalmotion.csr_api.service.check;

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

public class FusionCheckerTest {

	private FusionChecker checker;

	private JsonArray carList;

	@Before
	public void setup() throws IOException, NsbException {
		new NsbEditedTest().backup();
		checker = new FusionChecker();
		NsbReader reader = new NsbReader();
		File nsbFile = reader.getNsbFile("target");
		JsonObject nsbObject = new JsonBuilder().readJsonObject(nsbFile);
		carList = nsbObject.getJsonArray("caow");
	}

	@After
	public void after() throws IOException {
		new NsbEditedTest().restore();
	}

	@Test
	public void testCheckCarHyperFusion() throws Exception {
		JsonObject carHyperFusion = findCar(2); // "BMW_M235iCoupe_2014"
		CheckReport report = checker.checkCar(carHyperFusion);
		Assertions.assertThat(report).isNotNull();
		Assertions.assertThat(report.getError()).isEqualTo(ErrorType.FUSION_MAX);
		Assertions.assertThat(report.getMessage())
				.isEqualTo("BMW_M235iCoupe_2014: Maximum autorisé = 45, actuellement = 48");
	}

	@Test
	public void testCheckCarFull() throws Exception {
		JsonObject carHyperFusion = findCar(219); // "Bugatti_Divo_2019"
		Assertions.assertThat(checker.checkCar(carHyperFusion)).isNull();
	}

	private JsonObject findCar(int carId) throws NsbException {

		JsonObject carHyperFusion = null;
		int i = 0;
		while (carHyperFusion == null && i < carList.size()) {
			JsonObject actual = carList.getJsonObject(i++);
			if (actual.getInt("unid") == (carId)) {
				carHyperFusion = actual;
			}
		}
		return carHyperFusion;
	}
}
