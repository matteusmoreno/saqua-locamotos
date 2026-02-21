package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.domain.controller.AuthController;
import br.com.matteusmoreno.domain.dto.request.LoginRequestDto;
import br.com.matteusmoreno.domain.dto.response.LoginResponseDto;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.apache.http.auth.InvalidCredentialsException;

@Path("/auth")
public class AuthResource {

    private final AuthController authController;

    public AuthResource(AuthController authController) {
        this.authController = authController;
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequestDto loginRequestDto) throws InvalidCredentialsException {
        LoginResponseDto token = this.authController.login(loginRequestDto);

        return Response.status(HttpStatus.SC_OK).entity(token).build();
    }
}
