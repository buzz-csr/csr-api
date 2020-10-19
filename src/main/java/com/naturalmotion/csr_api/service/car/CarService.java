package com.naturalmotion.csr_api.service.car;

import javax.json.JsonObject;

public interface CarService {

    public JsonObject replace(int idToReplace, String newCarPath) throws CarException;

    public JsonObject full(int id) throws CarException;

    public JsonObject add(String newCarPath) throws CarException, NsbException;

    public JsonObject elite(int id) throws CarException, NsbException;
}
