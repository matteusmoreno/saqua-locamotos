package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class InvalidCurrentPasswordException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.INVALID_CURRENT_PASSWORD;
    private static final Integer STATUS_CODE = 400;
    private static final String DEFAULT_MESSAGE = Errors.INVALID_CURRENT_PASSWORD.getDisplayName();

    public InvalidCurrentPasswordException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }
}
