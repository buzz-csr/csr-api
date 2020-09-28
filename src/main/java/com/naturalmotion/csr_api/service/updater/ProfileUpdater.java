package com.naturalmotion.csr_api.service.updater;

import java.math.BigDecimal;

import com.naturalmotion.csr_api.api.ResourceType;

public interface ProfileUpdater {

    public void updateResource(ResourceType type, BigDecimal expected) throws UpdaterException;
}
