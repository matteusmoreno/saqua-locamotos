package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.LoginRequestDto;
import br.com.matteusmoreno.domain.dto.response.LoginResponseDto;
import br.com.matteusmoreno.domain.service.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.auth.InvalidCredentialsException;

@ApplicationScoped
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws InvalidCredentialsException {
        return this.authService.login(loginRequestDto);
    }
}
