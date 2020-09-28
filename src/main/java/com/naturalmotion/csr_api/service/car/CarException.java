package com.naturalmotion.csr_api.service.car;

import com.naturalmotion.csr_api.service.http.HttpCsrExcetion;

public class CarException extends Throwable {

    public CarException(String s) {
        super(s);
    }

    public CarException(Exception e) {
        super(e);
    }
}
