package com.naturalmotion.csr_api.service.updater;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.naturalmotion.csr_api.api.Resource;
import com.naturalmotion.csr_api.api.ResourceType;
import com.naturalmotion.csr_api.service.reader.ProfileReader;
import com.naturalmotion.csr_api.service.reader.ProfileReaderFileImpl;

public class ProfileUpdaterFileImplTest {

    @Before
    public void setup() throws IOException {
        clean();

        File original = new File("src/test/resources/Edited");
        Path copied = Paths.get("target/Edited");
        Path originalPath = original.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        File[] listFiles = original.listFiles();
        for (File file : listFiles) {
            copied = Paths.get("target/Edited/" + file.getName());
            originalPath = file.toPath();
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void clean() throws IOException {
        File file = new File("target/Edited");
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            for (File toDelete : listFiles) {
                Files.deleteIfExists(toDelete.toPath());
            }
            Files.deleteIfExists(file.toPath());
        }
    }

    @After
    public void after() throws IOException {
        clean();
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
