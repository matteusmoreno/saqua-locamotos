package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class ExpenseNotFoundException extends SaquaLocamotosException{

    private static final Errors ERROR_CODE = Errors.EXPENSE_NOT_FOUND;
    private static final Integer STATUS_CODE = 404;
    private static final String DEFAULT_MESSAGE = Errors.EXPENSE_NOT_FOUND.getDisplayName();

    public ExpenseNotFoundException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }
}
