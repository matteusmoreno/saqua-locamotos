package br.com.matteusmoreno.application.common;

import br.com.matteusmoreno.domain.constant.UserRole;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;


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

    public String getUserId() {
        Object userId = jwt.getClaim("userId");
        return userId != null ? userId.toString() : null;
    }

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

    public boolean isOwnerOrAdmin(String resourceOwnerId) {
        if (isAnonymous()) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        String loggedInUserId = getUserId();
        return loggedInUserId != null && loggedInUserId.equalsIgnoreCase(resourceOwnerId);
    }

    public void validateOwnerOrAdmin(String resourceOwnerId) {
        if (!isOwnerOrAdmin(resourceOwnerId)) {
            log.warn("Access denied. Logged-in user {} is not the owner (owner: {}) and is not an Admin.", getUserId(), resourceOwnerId);
            throw new ForbiddenException("Você não tem permissão para acessar ou realizar esta ação neste recurso.");
        }
    }

}

