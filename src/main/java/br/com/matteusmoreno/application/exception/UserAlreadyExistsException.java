package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class UserAlreadyExistsException extends SaquaLocamotosException {

    private static final Errors ERROR_CODE = Errors.USER_ALREADY_EXISTS;
    private static final Integer STATUS_CODE = 409;

    public UserAlreadyExistsException(String field) {
        super(field + " already exists", STATUS_CODE, ERROR_CODE);
    }
}

