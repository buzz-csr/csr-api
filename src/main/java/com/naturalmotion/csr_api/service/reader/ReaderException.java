package com.naturalmotion.csr_api.service.reader;

import java.io.IOException;

public class ReaderException extends Exception {

    private static final long serialVersionUID = -8819481754791753109L;

    public ReaderException(String msg) {
        super(msg);
    }

    public ReaderException(String string, IOException e) {
        super(string, e);
    }

}
