package com.naturalmotion.csr_api.service.updater;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naturalmotion.csr_api.api.Resource;
import com.naturalmotion.csr_api.api.ResourceType;
import com.naturalmotion.csr_api.service.NsbEditedTest;
import com.naturalmotion.csr_api.service.reader.ProfileReader;
import com.naturalmotion.csr_api.service.reader.ProfileReaderFileImpl;

public class ProfileUpdaterFileImplTest {

    @Before
    public void setup() throws IOException {
        new NsbEditedTest().backup();
    }

    @After
    public void after() throws IOException {
        new NsbEditedTest().restore();
    }

    @Test
    public void testUpdateResource() throws Exception {
        ProfileUpdater updater = new ProfileUpdaterFileImpl("target");
        updater.updateResource(ResourceType.CASH, new BigDecimal("500000"));

        ProfileReader reader = new ProfileReaderFileImpl("target");
        List<Resource> resources = reader.getResources();
        Resource actual =
                resources.stream().filter(x -> x.getType().equals(ResourceType.CASH)).findFirst().orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getSpent()).as("Cash spent").isEqualByComparingTo(new BigDecimal("1026956361"));
    }

}
