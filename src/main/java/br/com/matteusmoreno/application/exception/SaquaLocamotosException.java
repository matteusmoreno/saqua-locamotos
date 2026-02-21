package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.constant.Errors;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Getter
public abstract class SaquaLocamotosException extends RuntimeException {

    private final Integer httpStatus;
    private final Errors errorCode;
    private final LocalDateTime timestamp = LocalDateTime.now();

    protected SaquaLocamotosException(String message, Integer httpStatus, Errors errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public String getStackTraceAsString() {
        StringWriter sw = new StringWriter();
        this.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
