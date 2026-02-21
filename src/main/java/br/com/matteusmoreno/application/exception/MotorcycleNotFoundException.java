package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;


public class MotorcycleNotFoundException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.MOTORCYCLE_NOT_FOUND;
    private static final Integer STATUS_CODE = 404;
    private static final String DEFAULT_MESSAGE = Errors.MOTORCYCLE_NOT_FOUND.getDisplayName();

    public MotorcycleNotFoundException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }
}
