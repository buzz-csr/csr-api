package com.naturalmotion.csr_api.service.reader;

import java.util.List;

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

}
