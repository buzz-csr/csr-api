package com.naturalmotion.csr_api.service.car;

import com.naturalmotion.csr_api.service.reader.ReaderException;

public interface CarService {

    public void replace(String idToReplace, String newCarPath);

    public void full(String id) throws CarException;

    public void add(String newCarPath) throws CarException;
}
