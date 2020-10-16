package com.naturalmotion.csr_api.service.car;

import com.naturalmotion.csr_api.service.io.NsbException;

import javax.json.JsonObject;

public interface CarService {

    public JsonObject replace(int idToReplace, String newCarPath) throws CarException, NsbException;

    public JsonObject full(int id) throws CarException, NsbException;

    public void add(String newCarPath) throws CarException, NsbException;
}
