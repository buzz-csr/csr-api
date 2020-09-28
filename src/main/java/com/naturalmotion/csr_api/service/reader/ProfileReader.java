package com.naturalmotion.csr_api.service.reader;

import java.util.List;

import com.naturalmotion.csr_api.api.Resource;

public interface ProfileReader {

    public List<Resource> getResources() throws ReaderException;
}
