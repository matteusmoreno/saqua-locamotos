package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.exception.MotorcycleNotAvailableException;
import br.com.matteusmoreno.application.exception.SaquaLocamotosException;
import br.com.matteusmoreno.application.exception.UserAlreadyExistsException;
import br.com.matteusmoreno.domain.constant.UserRole;
import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.entity.Motorcycle;
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
    private final MotorcycleService motorcycleService;
    private final ErrorService errorService;
    private final UriInfo uriInfo;

    public UserService(UserRepository userRepository, AddressService addressService, PasswordService passwordService, MotorcycleService motorcycleService, ErrorService errorService, UriInfo uriInfo) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordService = passwordService;
        this.motorcycleService = motorcycleService;
        this.errorService = errorService;
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

    public User addMotorcycle(String userId, String motorcycleId) {
        log.info("Adding motorcycle with ID: {} to user with ID: {}", motorcycleId, userId);
        User user = this.findUserById(userId);
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(motorcycleId);

        try {
            this.validateMotorcycleAvailability(motorcycle);
            user.getMotorcycles().add(motorcycle);
            this.userRepository.update(user);
            log.info("Motorcycle with ID: {} added to user with ID: {}", motorcycleId, userId);
        } catch (SaquaLocamotosException e) {
            this.errorService.saveUserErrorInfo(user, e, uriInfo.getPath());
            throw e;
        }

        return user;
    }

    protected void validateMotorcycleAvailability(Motorcycle motorcycle) {
        log.info("Validating motorcycle availability for motorcycle with ID: {}", motorcycle.getMotorcycleId());
        if (!motorcycle.getAvailable()) {
            throw new MotorcycleNotAvailableException();
        }
    }

    protected void validateExistingEmailOrCpfOrRgOrPhone(String email, String cpf, String rg, String phone) {
        log.info("Validating existing email or cpf or RG or Phone number");
        if (this.userRepository.find("email", email).firstResult() != null) {
            throw new UserAlreadyExistsException("Email");
        }
        if (this.userRepository.find("cpf", cpf).firstResult() != null) {
            throw new UserAlreadyExistsException("CPF");
        }
        if (this.userRepository.find("rg", rg).firstResult() != null) {
            throw new UserAlreadyExistsException("RG");
        }
        if (this.userRepository.find("phone", phone).firstResult() != null) {
            throw new UserAlreadyExistsException("Phone");
        }
    }
}
