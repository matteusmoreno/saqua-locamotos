package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class InvalidLoginException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.INVALID_LOGIN;
    private static final Integer STATUS_CODE = 401;
    private static final String DEFAULT_MESSAGE = Errors.INVALID_LOGIN.getDisplayName();

    public InvalidLoginException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }
}
