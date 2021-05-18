package com.naturalmotion.csr_api.service.car;

import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import com.naturalmotion.csr_api.service.car.comparator.ComparatorParameter;
import com.naturalmotion.csr_api.service.io.NsbException;

public interface CarService {

	public JsonObject replace(int idToReplace, String newCarPath) throws CarException, NsbException;

	public JsonObject full(int id) throws CarException, NsbException;

	public JsonObject add(String newCarPath) throws CarException, NsbException;

	public JsonObject addId(String carId) throws CarException, NsbException;

	public JsonObject elite(int id) throws CarException, NsbException;

	public JsonObject removeElite(int id) throws CarException, NsbException;

	public JsonArray removeEliteLevel() throws NsbException;

	public JsonObject sort(ComparatorParameter param, boolean eliteFirst) throws NsbException;

	public List<String> getEliteCars() throws NsbException;

	public List<String> listAll() throws NsbException;
}
