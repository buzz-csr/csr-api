package com.naturalmotion.csr_api.service.car;

import java.util.List;

import javax.json.JsonObject;

import com.naturalmotion.csr_api.service.io.NsbException;

public interface CarService {

	public JsonObject replace(int idToReplace, String newCarPath) throws CarException, NsbException;

	public JsonObject full(int id) throws CarException, NsbException;

	public JsonObject add(String newCarPath) throws CarException, NsbException;

	public JsonObject elite(int id) throws CarException, NsbException;

	public JsonObject removeEliteLevel() throws NsbException;

	public JsonObject sort() throws NsbException;

	public List<String> getEliteCars() throws NsbException;
}
