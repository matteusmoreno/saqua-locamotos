package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.domain.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String jwtIssuer;
    @ConfigProperty(name = "app.saqua-locamotos.token.expiration.hours")
    Integer tokenExpirationInHours;


    public String generateToken(User user) {
        log.info("Generating JWT token for userId: {}", user.getUserId());
        return Jwt.issuer(jwtIssuer)
                .subject(user.getUserId())
                .upn(user.getEmail())
                .groups(user.getRole().name())
                .expiresIn(Duration.ofHours(tokenExpirationInHours))
                .sign();
    }

}
