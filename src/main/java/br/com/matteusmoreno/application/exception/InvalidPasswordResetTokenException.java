package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class InvalidPasswordResetTokenException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.INVALID_PASSWORD_RESET_TOKEN;
    private static final Integer STATUS_CODE = 400;
    private static final String DEFAULT_MESSAGE = Errors.INVALID_PASSWORD_RESET_TOKEN.getDisplayName();

    public InvalidPasswordResetTokenException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }
}

