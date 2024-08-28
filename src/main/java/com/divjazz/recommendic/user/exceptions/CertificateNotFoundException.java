package com.divjazz.recommendic.user.exceptions;

import com.divjazz.recommendic.user.model.Consultant;

public class CertificateNotFoundException extends RuntimeException{

    private static final String  ERROR_MESSAGE = "The Consultant %s does not have a certificate attached";

    public CertificateNotFoundException(String consultantName) {
        super(String.
                format(ERROR_MESSAGE, consultantName));
    }
}
