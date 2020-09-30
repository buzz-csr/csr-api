package com.naturalmotion.csr_api.service.car;

public interface CarService {

    public void replace(int idToReplace, String newCarPath) throws CarException;

    public void full(int id) throws CarException;

    public void add(String newCarPath) throws CarException;
}
