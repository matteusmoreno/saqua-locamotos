package br.com.matteusmoreno.application.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
public class DateUtils {

    @ConfigProperty(name = "app.saqua-locamotos.timezone")
    String TIMEZONE;

    public LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(TIMEZONE));
    }

    public LocalDate today() {
        return LocalDate.now(ZoneId.of(TIMEZONE));
    }
}
