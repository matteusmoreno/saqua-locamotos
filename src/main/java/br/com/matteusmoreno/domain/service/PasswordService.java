package br.com.matteusmoreno.domain.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PasswordService {

    public String encryptPassword(String password) {
        log.info("Encrypting password");
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
