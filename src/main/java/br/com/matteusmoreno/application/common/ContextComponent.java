package br.com.matteusmoreno.application.common;

import br.com.matteusmoreno.application.utils.DateUtils;
import br.com.matteusmoreno.domain.constant.UserRole;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@RequestScoped
@Slf4j
public class ContextComponent {

    @ConfigProperty(name = "app.saqua.locamotos.admin.secret-key")
    String appSecretKey;

    private final JsonWebToken jwt;
    private final SecurityIdentity identity;

    public ContextComponent(JsonWebToken jwt, SecurityIdentity identity) {
        this.jwt = jwt;
        this.identity = identity;
    }

    public UUID getUserId() {return jwt.getClaim("userId");}

    public String getEmail() {
        return jwt.getClaim("upn");
    }

    public String getName() {
        return jwt.getClaim("name");
    }

    public String getRole() {
        return identity.getRoles().stream().findFirst().orElse(null);
    }

    public boolean hasRole(String role) {
        return identity.hasRole(role);
    }

    public boolean isAnonymous() {
        return identity.isAnonymous();
    }

    public boolean isAdmin() {
        return this.hasRole(UserRole.ADMIN.name());
    }

    public void authenticateCriticalAction(String secretKey) {
        boolean isAdmin = this.isAdmin();
        boolean validSecretKey = secretKey.equals(this.appSecretKey);

        if (!isAdmin || !validSecretKey) {
            log.warn("Unauthorized attempt to perform critical action. User: {}, Role: {}, Valid Secret Key: {}", this.getEmail(), this.getRole(), validSecretKey);
            throw new ForbiddenException();
        }
    }

}

