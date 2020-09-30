package com.naturalmotion.csr_api.service.car;

public class CarException extends Exception {

    private static final long serialVersionUID = -8251551617551347910L;

    public CarException(String s) {
        super(s);
    }

    public CarException(Exception e) {
        super(e);
    }
}
