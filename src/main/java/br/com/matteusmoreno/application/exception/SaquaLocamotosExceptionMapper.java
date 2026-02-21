package br.com.matteusmoreno.application.exception;

import br.com.matteusmoreno.domain.model.ErrorInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SaquaLocamotosExceptionMapper implements ExceptionMapper<SaquaLocamotosException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(SaquaLocamotosException exception) {

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode(exception.getErrorCode().name())
                .message(exception.getMessage())
                .status(exception.getHttpStatus())
                .path(uriInfo.getPath())
                .timestamp(exception.getTimestamp())
                .build();

        return Response.status(exception.getHttpStatus()).entity(errorInfo).build();
    }
}

