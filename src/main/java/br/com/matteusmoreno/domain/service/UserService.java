package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.exception.*;
import br.com.matteusmoreno.application.service.CloudinaryService;
import br.com.matteusmoreno.application.service.EmailService;
import br.com.matteusmoreno.application.utils.DateUtils;
import br.com.matteusmoreno.domain.constant.CloudinaryFolder;
import br.com.matteusmoreno.domain.constant.UserRole;
import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.dto.request.ResetPasswordRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdatePasswordRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdateUserRequestDto;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.model.Address;
import br.com.matteusmoreno.domain.model.UserDocument;
import br.com.matteusmoreno.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final PasswordService passwordService;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final JsonWebToken jwt;
    private final ErrorService errorService;
    private final UriInfo uriInfo;
    private final DateUtils dateUtils;

    public UserService(UserRepository userRepository, AddressService addressService, PasswordService passwordService, CloudinaryService cloudinaryService, EmailService emailService, JsonWebToken jwt, ErrorService errorService, UriInfo uriInfo, DateUtils dateUtils) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordService = passwordService;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
        this.jwt = jwt;
        this.errorService = errorService;
        this.uriInfo = uriInfo;
        this.dateUtils = dateUtils;
    }

    public User createCustomer(CreateUserRequestDto request) {
        this.validateExistingEmailOrCpfOrRgOrPhone(request.email(), request.cpf(), request.rg(), request.phone());
        Address address = this.addressService.getAddress(request.address());
        String passwordHash = this.passwordService.encryptPassword(request.cpf());
        LocalDateTime now = LocalDateTime.now();

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
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.userRepository.persist(user);
        log.info("Customer created with ID: {}", user.getUserId());

        this.emailService.sendWelcomeEmail(user.getName(), user.getEmail(), user.getCpf());

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

    public void sendVerificationEmail(String userId) {
        this.validateOwnership(userId);

        User user = this.findUserById(userId);
        this.validateIfEmailAlreadyVerified(user);

        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setUpdatedAt(LocalDateTime.now());
        this.userRepository.update(user);

        this.emailService.sendVerificationEmail(user.getName(), user.getEmail(), token);
        log.info("Verification email sent for user: {}", userId);
    }

    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);

        User user = this.userRepository.findByEmailVerificationToken(token);

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setUpdatedAt(LocalDateTime.now());
        this.userRepository.update(user);

        log.info("Email verified successfully for user: {}", user.getUserId());
    }

    public void sendResetPasswordEmail(String email) {
        log.info("Sending password reset email to: {}", email);

        User user = this.findUserByEmail(email);
        this.validateIfUserHasEmailVerified(user);

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setUpdatedAt(LocalDateTime.now());
        this.userRepository.update(user);

        this.emailService.sendResetPasswordEmail(user.getName(), user.getEmail(), token);
        log.info("Password reset email sent for user: {}", user.getUserId());
    }

    public void resetPassword(ResetPasswordRequestDto request) {
        log.info("Resetting password with token: {}", request.token());

        User user = this.userRepository.findByPasswordResetToken(request.token());

        LocalDateTime now = LocalDateTime.now();
        String newPasswordHash = this.passwordService.encryptPassword(request.newPassword());
        user.setPassword(newPasswordHash);
        user.setPasswordResetToken(null);
        user.setUpdatedAt(now);
        this.userRepository.update(user);

        String changedAt = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss"));
        this.emailService.sendPasswordChangedEmail(user.getName(), user.getEmail(), changedAt);

        log.info("Password reset successfully for user: {}", user.getUserId());
    }

    public User uploadPicture(String userId, byte[] fileBytes) {
        User user = this.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (user.getPictureUrl() != null && !user.getPictureUrl().isBlank()) {
            String oldPublicId = this.cloudinaryService.extractPublicId(user.getPictureUrl());
            this.cloudinaryService.delete(oldPublicId, CloudinaryFolder.USER_PICTURE.getResourceType());
        }

        String url = this.cloudinaryService.upload(fileBytes, user.getUserId(), CloudinaryFolder.USER_PICTURE);
        user.setPictureUrl(url);
        user.setUpdatedAt(now);
        this.userRepository.update(user);

        log.info("Picture uploaded for user: {}", userId);
        return user;
    }

    public User deletePicture(String userId) {
        User user = this.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (user.getPictureUrl() == null || user.getPictureUrl().isBlank()) {
            throw new PictureNotFoundException();
        }

        this.cloudinaryService.delete(this.cloudinaryService.extractPublicId(user.getPictureUrl()), CloudinaryFolder.USER_PICTURE.getResourceType());
        user.setPictureUrl(null);
        user.setUpdatedAt(now);
        this.userRepository.update(user);

        log.info("Picture deleted for user: {}", userId);
        return user;
    }

    public User uploadDocuments(String userId, Map<String, byte[]> documents) {
        this.validateOwnership(userId);
        User user = this.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (user.getDocuments() == null) {
            user.setDocuments(new UserDocument());
        }

        for (Map.Entry<String, byte[]> entry : documents.entrySet()) {
            String documentType = entry.getKey().toLowerCase();
            byte[] fileBytes = entry.getValue();

            String publicId = user.getUserId() + "_" + documentType;

            // Delete old file if exists
            String existingUrl = getDocumentUrl(user, documentType);
            if (existingUrl != null && !existingUrl.isBlank()) {
                this.cloudinaryService.delete(this.cloudinaryService.extractPublicId(existingUrl), CloudinaryFolder.USER_DOCUMENT.getResourceType());
            }

            String url = this.cloudinaryService.upload(fileBytes, publicId, CloudinaryFolder.USER_DOCUMENT);
            setDocumentUrl(user, documentType, url);
            log.info("Document {} uploaded for user: {}", documentType, userId);
        }

        user.setUpdatedAt(now);
        this.userRepository.update(user);
        return user;
    }

    public User deleteDocuments(String userId, List<String> documentTypes) {
        this.validateOwnership(userId);
        User user = this.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        for (String documentType : documentTypes) {
            String existingUrl = getDocumentUrl(user, documentType.toLowerCase());
            if (existingUrl == null || existingUrl.isBlank()) {
                throw new DocumentNotFoundException();
            }

            this.cloudinaryService.delete(this.cloudinaryService.extractPublicId(existingUrl), CloudinaryFolder.USER_DOCUMENT.getResourceType());
            setDocumentUrl(user, documentType.toLowerCase(), null);
            log.info("Document {} deleted for user: {}", documentType, userId);
        }

        user.setUpdatedAt(now);
        this.userRepository.update(user);
        return user;
    }

    public User updateUser(UpdateUserRequestDto request) {
        log.info("Updating user with ID: {}", request.userId());
        User user = this.findUserById(request.userId());
        LocalDateTime now = LocalDateTime.now();

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

        user.setUpdatedAt(now);

        this.userRepository.update(user);
        log.info("User with ID: {} updated", request.userId());

        return user;
    }

    public void updatePassword(UpdatePasswordRequestDto request) {
        log.info("Updating password for user ID: {}", request.userId());
        this.validateOwnership(request.userId());

        User user = this.findUserById(request.userId());
        this.validateCurrentPassword(user, request.currentPassword());

        LocalDateTime now = this.dateUtils.now();
        String newPasswordHash = this.passwordService.encryptPassword(request.newPassword());
        user.setPassword(newPasswordHash);
        user.setUpdatedAt(now);

        this.userRepository.update(user);

        String changedAt = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss"));
        this.emailService.sendPasswordChangedEmail(user.getName(), user.getEmail(), changedAt);

        log.info("Password updated for user ID: {}", request.userId());
    }

    protected void validateOwnership(String userId) {
        boolean isAdmin = jwt.getGroups().contains("ADMIN");
        if (!isAdmin && !jwt.getSubject().equals(userId)) {
            log.warn("Access denied: caller {} tried to access user {}", jwt.getSubject(), userId);
            throw new UnauthorizedAccessException();
        }
    }

    protected String getDocumentUrl(User user, String documentType) {
        return switch (documentType) {
            case "cnh" -> user.getDocuments().getCnh();
            case "cpf" -> user.getDocuments().getCpfUrl();
            case "rg" -> user.getDocuments().getRgUrl();
            case "proof_of_residence" -> user.getDocuments().getProofOfResidenceUrl();
            case "criminal_record" -> user.getDocuments().getCriminalRecordUrl();
            case "passport" -> user.getDocuments().getPassportUrl();
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        };
    }

    protected void setDocumentUrl(User user, String documentType, String url) {
        switch (documentType) {
            case "cnh" -> user.getDocuments().setCnh(url);
            case "cpf" -> user.getDocuments().setCpfUrl(url);
            case "rg" -> user.getDocuments().setRgUrl(url);
            case "proof_of_residence" -> user.getDocuments().setProofOfResidenceUrl(url);
            case "criminal_record" -> user.getDocuments().setCriminalRecordUrl(url);
            case "passport" -> user.getDocuments().setPassportUrl(url);
            default -> throw new IllegalArgumentException("Invalid document type: " + documentType);
        }
    }

    protected void validateExistingEmailOrCpfOrRgOrPhone(String email, String cpf, String rg, String phone) {
        log.info("Validating existing email or cpf or RG or Phone number");
        if (this.userRepository.find("email", email).firstResult() != null) throw new UserAlreadyExistsException("Email");
        if (this.userRepository.find("cpf", cpf).firstResult() != null) throw new UserAlreadyExistsException("CPF");
        if (this.userRepository.find("rg", rg).firstResult() != null) throw new UserAlreadyExistsException("RG");
        if (this.userRepository.find("phone", phone).firstResult() != null) throw new UserAlreadyExistsException("Phone");
    }

    protected void validateIfEmailAlreadyVerified(User user) {
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            log.warn("Email already verified for user: {}", user.getUserId());
            String path = this.uriInfo.getPath();
            this.errorService.saveUserErrorInfo(user, new EmailAlreadyVerifiedException(), path);
            throw new EmailAlreadyVerifiedException();
        }
    }

    protected void validateIfUserHasEmailVerified(User user) {
        if (Boolean.FALSE.equals(user.getEmailVerified())) {
            log.warn("Email not verified for user: {}", user.getUserId());
            String path = this.uriInfo.getPath();
            this.errorService.saveUserErrorInfo(user, new EmailNotVerifiedException(), path);
            throw new EmailNotVerifiedException();
        }
    }

    protected void validateCurrentPassword(User user, String currentPassword) {
        if (!this.passwordService.verifyPassword(currentPassword, user.getPassword())) {
            log.warn("Invalid current password for user: {}", user.getUserId());
            String path = this.uriInfo.getPath();
            this.errorService.saveUserErrorInfo(user, new InvalidCurrentPasswordException(), path);
            throw new InvalidCurrentPasswordException();
        }
    }
}
