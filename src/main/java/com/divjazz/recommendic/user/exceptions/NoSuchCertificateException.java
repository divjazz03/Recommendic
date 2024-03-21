package com.divjazz.recommendic.user.exceptions;

public class NoSuchCertificateException extends RuntimeException {
    private static final String ERROR_MESSAGE = "The certificate type inputted is invalid, please replace it";

    public NoSuchCertificateException() {
        super(ERROR_MESSAGE);
    }
}
