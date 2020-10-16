package com.naturalmotion.csr_api.service.reader;

import java.util.List;

import com.naturalmotion.csr_api.service.io.NsbException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.naturalmotion.csr_api.api.Resource;

public class ProfileReaderFileImplTest {

    @Test
    public void testGetResources() throws Exception {
        ProfileReaderFileImpl reader = new ProfileReaderFileImpl("src/test/resources");
        List<Resource> resources = reader.getResources();
        Assertions.assertThat(resources).hasSize(5);
    }

    @Test
    public void testGetBrands() throws NsbException {
        ProfileReaderFileImpl reader = new ProfileReaderFileImpl("src/test/resources");
        List<String> brands = reader.getBrands();
        Assertions.assertThat(brands).hasSize(62);
    }
}
