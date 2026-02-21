package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class MotorcycleNotAssignedToUserException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.MOTORCYCLE_NOT_ASSIGNED_TO_USER;
    private static final Integer STATUS_CODE = 400;
    private static final String DEFAULT_MESSAGE = Errors.MOTORCYCLE_NOT_ASSIGNED_TO_USER.getDisplayName();

    public MotorcycleNotAssignedToUserException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }
}

