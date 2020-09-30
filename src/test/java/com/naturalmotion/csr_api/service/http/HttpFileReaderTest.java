package com.naturalmotion.csr_api.service.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class HttpFileReaderTest {

    @Test
    public void testRead() throws HttpCsrExcetion {
        Authenticator authenticator = new Authenticator() {

            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication("jdesachy", "Juillet$032020".toCharArray()));
            }
        };
        Authenticator.setDefault(authenticator);

        String read = new HttpFileReader().read("/Cars/Chevrolet/COPO Camaro/Black.txt");
        Assertions.assertThat(read).isNotNull();
    }
}