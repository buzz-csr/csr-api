package com.naturalmotion.csr_api.service.http;

import java.net.URI;

public class HttpCsrExcetion extends Exception {

    private static final long serialVersionUID = -7887163941000173027L;

    public HttpCsrExcetion(String path, Exception e) {
        super("Error reading file " + path, e);
    }
}
