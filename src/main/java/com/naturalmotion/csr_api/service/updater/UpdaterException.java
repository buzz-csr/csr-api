package com.naturalmotion.csr_api.service.updater;

public class UpdaterException extends Exception {

    private static final long serialVersionUID = -2503591195838380481L;

    public UpdaterException(String message) {
        super(message);
    }

    public UpdaterException(String string, Exception e) {
        super(string, e);
    }
}
