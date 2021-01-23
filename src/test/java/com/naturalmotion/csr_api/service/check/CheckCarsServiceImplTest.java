package com.naturalmotion.csr_api.service.check;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naturalmotion.csr_api.service.NsbEditedTest;

public class CheckCarsServiceImplTest {

	private CheckCarsServiceImpl service = new CheckCarsServiceImpl();

	@Before
	public void setup() throws IOException {
		new NsbEditedTest().backup();
	}

	@After
	public void after() throws IOException {
		new NsbEditedTest().restore();
	}

	@Test
	public void testCheck() throws Exception {
		List<CheckReport> check = service.check("target");
		Assertions.assertThat(check).hasSize(25);
	}

}
