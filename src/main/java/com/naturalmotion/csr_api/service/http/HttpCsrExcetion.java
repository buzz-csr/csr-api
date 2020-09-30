package com.naturalmotion.csr_api.service.http;

import java.net.URI;

public class HttpCsrExcetion extends Exception {

    private static final long serialVersionUID = -7887163941000173027L;

    public HttpCsrExcetion(URI uri, Exception e) {
        super("Error making http call " + uri, e);
    }
}
