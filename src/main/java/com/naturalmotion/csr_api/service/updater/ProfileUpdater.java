package com.naturalmotion.csr_api.service.updater;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.json.JsonObject;

import com.naturalmotion.csr_api.api.EliteTokenParam;
import com.naturalmotion.csr_api.api.ResourceType;
import com.naturalmotion.csr_api.service.io.NsbException;

public interface ProfileUpdater {

    public void updateResource(ResourceType type, BigDecimal expected) throws UpdaterException, NsbException;

    public JsonObject deban() throws NsbException;

    void updateResourceAfterBan(List<EliteTokenParam> tokens) throws UpdaterException, NsbException, IOException;
}
