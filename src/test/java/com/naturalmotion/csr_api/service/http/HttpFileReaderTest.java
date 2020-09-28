package com.naturalmotion.csr_api.service.http;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class HttpFileReaderTest {

    @Test
    public void testRead() throws HttpCsrExcetion {
        String read = new HttpFileReader().read("/Cars/Chevrolet/COPO Camaro/Black.txt");
        Assertions.assertThat(read).isNotNull();
    }
}