package com.naturalmotion.csr_api.service.check;

import java.util.List;

import com.naturalmotion.csr_api.service.io.NsbException;

public interface CheckCarsService {

	List<CheckReport> check(String path) throws NsbException;

	List<CheckReport> correct(String path) throws NsbException;

}
