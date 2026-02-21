package br.com.matteusmoreno.domain.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.matteusmoreno.application.exception.UserNotFoundException;
import br.com.matteusmoreno.domain.dto.request.LoginRequestDto;
import br.com.matteusmoreno.domain.dto.response.LoginResponseDto;
import br.com.matteusmoreno.domain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;

@ApplicationScoped
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponseDto login(LoginRequestDto request)
            throws InvalidCredentialsException, UserNotFoundException {
        log.info("Attempting to authenticate user with email: {}", request.email());

        User user = this.userService.findUserByEmail(request.email());

        BCrypt.Result result = BCrypt.verifyer().verify(request.password().toCharArray(), user.getPassword());
        if (!result.verified) {
            log.warn("Invalid password for email: {}", request.email());
            throw new InvalidCredentialsException();
        }

        log.info("User {} authenticated successfully", user.getUserId());
        String token = this.jwtService.generateToken(user);
        return new LoginResponseDto(token);

    }
}
