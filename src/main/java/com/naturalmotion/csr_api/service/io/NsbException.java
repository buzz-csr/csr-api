package com.naturalmotion.csr_api.service.io;

import java.io.FileNotFoundException;

public class NsbException extends Exception {

    public NsbException(String s) {
        super(s);
    }

    public NsbException(Exception e) {
        super(e);
    }
}
