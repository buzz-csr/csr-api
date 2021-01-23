package com.naturalmotion.csr_api.service.check;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naturalmotion.csr_api.service.NsbEditedTest;
import com.naturalmotion.csr_api.service.io.NsbException;

public class CheckCarsServiceImplTest {

	private CheckCarsServiceImpl service;

	@Before
	public void setup() throws IOException, NsbException {
		new NsbEditedTest().backup();
		service = new CheckCarsServiceImpl();
	}

	@After
	public void after() throws IOException {
		new NsbEditedTest().restore();
	}

	@Test
	public void testCheck() throws Exception {
		List<CheckReport> check = service.check("target");
		Assertions.assertThat(check).hasSize(29);
	}

}
