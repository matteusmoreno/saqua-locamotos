package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.exception.SaquaLocamotosException;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.model.ErrorInfo;
import br.com.matteusmoreno.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class ErrorService {

    private final UserRepository userRepository;

    public ErrorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUserErrorInfo(User user, SaquaLocamotosException exception, String path) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode(exception.getErrorCode().name())
                .message(exception.getMessage())
                .status(exception.getHttpStatus())
                .path(path)
                .stackTrace(exception.getStackTraceAsString())
                .timestamp(exception.getTimestamp())
                .build();

        user.getErrors().add(errorInfo);
        this.userRepository.update(user);
        log.warn("Error saved to user {}: [{}] {}", user.getUserId(), errorInfo.getStatus(), errorInfo.getMessage());
    }
}
