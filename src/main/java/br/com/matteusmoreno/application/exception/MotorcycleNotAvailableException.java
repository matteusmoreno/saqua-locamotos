package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;

public class MotorcycleNotAvailableException extends SaquaLocamotosException {

  private static final Errors ERROR_CODE = Errors.MOTORCYCLE_NOT_AVAILABLE;
  private static final Integer STATUS_CODE = 400;
  private static final String DEFAULT_MESSAGE = Errors.MOTORCYCLE_NOT_AVAILABLE.getDisplayName();

    public MotorcycleNotAvailableException() {
        super(DEFAULT_MESSAGE, STATUS_CODE, ERROR_CODE);
    }

}
