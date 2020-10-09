package com.naturalmotion.csr_api.service.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class HttpFileReaderTest {

    @Test
    public void testRead() throws HttpCsrExcetion {
        String read = new HttpFileReader().read("/Cars/Audi/2014 LB R8 V10 Plus Coupé/Ara Blue Crystal (2).txt");
        Assertions.assertThat(read).isNotNull();
    }
}