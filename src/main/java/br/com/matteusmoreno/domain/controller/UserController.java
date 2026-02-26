package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.dto.request.ResetPasswordRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdatePasswordRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdateUserRequestDto;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;

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

    public User uploadPicture(String userId, byte[] fileBytes) {
        return this.userService.uploadPicture(userId, fileBytes);
    }

    public User deletePicture(String userId) {
        return this.userService.deletePicture(userId);
    }

    public User uploadDocuments(String userId, Map<String, byte[]> documents) {
        return this.userService.uploadDocuments(userId, documents);
    }

    public User deleteDocuments(String userId, List<String> documentTypes) {
        return this.userService.deleteDocuments(userId, documentTypes);
    }

    public User updateUser(UpdateUserRequestDto request) {
        return this.userService.updateUser(request);
    }

    public void updatePassword(UpdatePasswordRequestDto request) {
        this.userService.updatePassword(request);
    }

    public void verifyEmail(String token) {
        this.userService.verifyEmail(token);
    }

    public void sendVerificationEmail(String userId) {
        this.userService.sendVerificationEmail(userId);
    }

    public void sendResetPasswordEmail(String email) {
        this.userService.sendResetPasswordEmail(email);
    }

    public void resetPassword(ResetPasswordRequestDto request) {
        this.userService.resetPassword(request);
    }
}
