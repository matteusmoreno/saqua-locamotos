package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.exception.PictureNotFoundException;
import br.com.matteusmoreno.application.exception.UserAlreadyExistsException;
import br.com.matteusmoreno.application.service.CloudinaryService;
import br.com.matteusmoreno.domain.constant.CloudinaryFolder;
import br.com.matteusmoreno.domain.constant.UserRole;
import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdateUserRequestDto;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.model.Address;
import br.com.matteusmoreno.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final PasswordService passwordService;
    private final ErrorService errorService;
    private final CloudinaryService cloudinaryService;
    private final UriInfo uriInfo;

    public UserService(UserRepository userRepository, AddressService addressService, PasswordService passwordService, ErrorService errorService, CloudinaryService cloudinaryService, UriInfo uriInfo) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordService = passwordService;
        this.errorService = errorService;
        this.cloudinaryService = cloudinaryService;
        this.uriInfo = uriInfo;
    }

    public User createCustomer(CreateUserRequestDto request) {

        this.validateExistingEmailOrCpfOrRgOrPhone(request.email(), request.cpf(), request.rg(), request.phone());
        Address address = this.addressService.getAddress(request.address());
        String passwordHash = this.passwordService.encryptPassword(request.cpf());

        log.info("Creating customer with email: {}", request.email());
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .emailVerified(false)
                .password(passwordHash)
                .phone(request.phone())
                .cpf(request.cpf())
                .rg(request.rg())
                .occupation(request.occupation())
                .maritalStatus(request.maritalStatus())
                .address(address)
                .pictureUrl(request.pictureUrl())
                .role(UserRole.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        this.userRepository.persist(user);
        log.info("Customer created with ID: {}", user.getUserId());

        return user;
    }

    public User findUserById(String userId) {
        log.info("Finding user with ID: {}", userId);
        return this.userRepository.findUserById(userId);
    }

    public List<User> findAllCustomers() {
        log.info("Finding all customers");
        return this.userRepository.findAllCustomer();
    }

    public User findUserByEmail(String email) {
        log.info("Finding user with email: {}", email);
        return this.userRepository.findUserByEmail(email);
    }

    public User uploadPicture(String userId, byte[] fileBytes) {
        User user = this.findUserById(userId);

        if (user.getPictureUrl() != null && !user.getPictureUrl().isBlank()) {
            String oldPublicId = this.cloudinaryService.extractPublicId(user.getPictureUrl());
            this.cloudinaryService.delete(oldPublicId);
        }

        String url = this.cloudinaryService.upload(fileBytes, user.getUserId(), CloudinaryFolder.USER_PICTURE);
        user.setPictureUrl(url);
        user.setUpdatedAt(LocalDateTime.now());
        this.userRepository.update(user);

        log.info("Picture uploaded for user: {}", userId);
        return user;
    }

    public User deletePicture(String userId) {
        User user = this.findUserById(userId);

        if (user.getPictureUrl() == null || user.getPictureUrl().isBlank()) {
            throw new PictureNotFoundException();
        }

        cloudinaryService.delete(cloudinaryService.extractPublicId(user.getPictureUrl()));
        user.setPictureUrl(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.update(user);

        log.info("Picture deleted for user: {}", userId);
        return user;
    }

    public User updateUser(UpdateUserRequestDto request) {
        log.info("Updating user with ID: {}", request.userId());
        User user = this.findUserById(request.userId());

        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.cpf() != null) user.setCpf(request.cpf());
        if (request.rg() != null) user.setRg(request.rg());
        if (request.occupation() != null) user.setOccupation(request.occupation());
        if (request.maritalStatus() != null) user.setMaritalStatus(request.maritalStatus());
        if (request.address() != null) {
            Address address = this.addressService.getAddress(request.address());
            user.setAddress(address);
        }

        user.setUpdatedAt(LocalDateTime.now());

        this.userRepository.update(user);
        log.info("User with ID: {} updated", request.userId());

        return user;
    }


    protected void validateExistingEmailOrCpfOrRgOrPhone(String email, String cpf, String rg, String phone) {
        log.info("Validating existing email or cpf or RG or Phone number");
        if (this.userRepository.find("email", email).firstResult() != null) throw new UserAlreadyExistsException("Email");
        if (this.userRepository.find("cpf", cpf).firstResult() != null) throw new UserAlreadyExistsException("CPF");
        if (this.userRepository.find("rg", rg).firstResult() != null) throw new UserAlreadyExistsException("RG");
        if (this.userRepository.find("phone", phone).firstResult() != null) throw new UserAlreadyExistsException("Phone");
    }
}
