package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User createUser(CreateUserRequestDto request) {
        return this.userService.createCustomer(request);
    }
}
