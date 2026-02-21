package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdateUserRequestDto;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User createUser(CreateUserRequestDto request) {
        return this.userService.createCustomer(request);
    }

    public User findUserById(String userId) {
        return this.userService.findUserById(userId);
    }

    public List<User> findAllUsers() {
        return this.userService.findAllCustomers();
    }

    public User addMotorcycle(String userId, String motorcycleId) {
        return this.userService.addMotorcycle(userId, motorcycleId);
    }

    public User removeMotorcycle(String userId, String motorcycleId) {
        return this.userService.removeMotorcycle(userId, motorcycleId);
    }

    public User uploadPicture(String userId, byte[] fileBytes) {
        return this.userService.uploadPicture(userId, fileBytes);
    }

    public User updateUser(UpdateUserRequestDto request) {
        return this.userService.updateUser(request);
    }
}
