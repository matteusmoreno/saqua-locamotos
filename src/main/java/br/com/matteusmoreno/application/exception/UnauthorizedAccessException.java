package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class UnauthorizedAccessException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.UNAUTHORIZED_ACCESS;
    private static final Integer STATUS_CODE = 403;
    private static final String DEFAULT_MESSAGE = Errors.UNAUTHORIZED_ACCESS.getDisplayName();

    public UnauthorizedAccessException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }

    public UnauthorizedAccessException(String message) {
        super(message, STATUS_CODE, ERROR_CODE);
    }
}
