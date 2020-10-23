package com.naturalmotion.csr_api.service.updater;

import java.math.BigDecimal;

import com.naturalmotion.csr_api.api.ResourceType;
import com.naturalmotion.csr_api.service.io.NsbException;

public interface ProfileUpdater {

    public void updateResource(ResourceType type, BigDecimal expected) throws UpdaterException, NsbException;

    public void deban() throws NsbException;
}
