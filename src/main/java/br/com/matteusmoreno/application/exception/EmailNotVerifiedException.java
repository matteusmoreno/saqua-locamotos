package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class EmailNotVerifiedException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.EMAIL_NOT_VERIFIED;
    private static final Integer STATUS_CODE = 403;
    private static final String DEFAULT_MESSAGE = Errors.EMAIL_NOT_VERIFIED.getDisplayName();

    public EmailNotVerifiedException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }

}
