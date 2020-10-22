package com.naturalmotion.csr_api.service.car;

import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CgpiUpdaterTest {

    private CgpiUpdater updater = new CgpiUpdater();

    @Test
    public void updateGarageNotFull() {
        JsonArrayBuilder cgpi = Json.createArrayBuilder(Arrays.asList(0, -1, -1, -1, -1, -1));
        JsonArrayBuilder update = updater.update(cgpi.build(), 1);
        Assertions.assertThat(update.build().toString()).isEqualTo("[0,1,-1,-1,-1,-1]");
    }

    @Test
    public void updateGarageFull() {
        JsonArrayBuilder cgpi = Json.createArrayBuilder(Arrays.asList(0, 2, 3, 4, 5, 1));
        JsonArrayBuilder update = updater.update(cgpi.build(), 6);
        Assertions.assertThat(update.build().toString()).isEqualTo("[0,2,3,4,5,1,6,-1,-1,-1,-1,-1]");
    }
}