package br.com.matteusmoreno.application.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@ApplicationScoped
@Slf4j
public class EmailService {

    private final Template welcomeTemplate;
    private final Template verifyEmailTemplate;
    private final Mailer mailer;


    public EmailService(
            Mailer mailer,
            @Location("emails/welcome.html") Template welcomeTemplate,
            @Location("emails/verify-email.html") Template verifyEmailTemplate) {
        this.mailer = mailer;
        this.welcomeTemplate = welcomeTemplate;
        this.verifyEmailTemplate = verifyEmailTemplate;
    }

    public void sendWelcomeEmail(String name, String email, String cpf) {
        log.info("Sending welcome email to: {}", email);

        String htmlBody = welcomeTemplate
                .data("name", name)
                .data("email", email)
                .data("cpf", cpf)
                .render();

        byte[] logoBytes = loadLogoBytes();

        Mail mail = Mail.withHtml(email, "Bem-vindo à Saqua Locamotos! 🏍️", htmlBody)
                .addInlineAttachment("logo", logoBytes, "image/png", "<logo>");

        try {
            this.mailer.send(mail);
            log.info("Welcome email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", email, e.getMessage());
        }
    }

    public void sendVerificationEmail(String name, String email, String token) {
        log.info("Sending verification email to: {}", email);

        String htmlBody = this.verifyEmailTemplate
                .data("name", name)
                .data("token", token)
                .render();

        byte[] logoBytes = loadLogoBytes();

        Mail mail = Mail.withHtml(email, "Confirme seu e-mail — Saqua Locamotos ✉️", htmlBody)
                .addInlineAttachment("logo", logoBytes, "image/png", "<logo>");

        try {
            this.mailer.send(mail);
            log.info("Verification email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage());
        }
    }

    protected byte[] loadLogoBytes() {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("images/saqua-locamotos-logo-removebg-preview.png")) {
            if (is == null) {
                log.warn("Logo image not found in resources/images");
                return new byte[0];
            }
            return is.readAllBytes();
        } catch (Exception e) {
            log.error("Failed to load logo image: {}", e.getMessage());
            return new byte[0];
        }
    }
}
