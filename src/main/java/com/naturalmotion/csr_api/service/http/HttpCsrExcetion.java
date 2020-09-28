package com.naturalmotion.csr_api.service.http;

public class HttpCsrExcetion extends Exception {

    public HttpCsrExcetion(Exception e) {
        super("Error making http call", e);
    }
}
