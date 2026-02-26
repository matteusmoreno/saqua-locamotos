package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class EmailAlreadyVerifiedException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.EMAIL_ALREADY_VERIFIED;
    private static final Integer STATUS_CODE = 400;
    private static final String DEFAULT_MESSAGE = Errors.EMAIL_ALREADY_VERIFIED.getDisplayName();

    public EmailAlreadyVerifiedException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }
}
