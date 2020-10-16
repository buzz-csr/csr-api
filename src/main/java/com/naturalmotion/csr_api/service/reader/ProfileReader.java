package com.naturalmotion.csr_api.service.reader;

import java.util.List;

import com.naturalmotion.csr_api.api.Resource;
import com.naturalmotion.csr_api.service.car.CarException;
import com.naturalmotion.csr_api.service.io.NsbException;

public interface ProfileReader {

    List<Resource> getResources() throws ReaderException;

    List<String> getBrands() throws NsbException;
}
