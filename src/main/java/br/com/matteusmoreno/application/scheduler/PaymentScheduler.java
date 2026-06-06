package br.com.matteusmoreno.application.scheduler;

import br.com.matteusmoreno.domain.service.PaymentService;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PaymentScheduler {

    private final PaymentService paymentService;

    public PaymentScheduler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Roda todos os dias à meia-noite e um minuto
    @Scheduled(cron = "0 1 0 * * ?")
    public void markOverduePaymentsRoutine() {
        log.info("Scheduled execution (CRON): Checking for overdue payments...");
        this.executeRoutine();
    }

    // Roda sempre que a aplicação termina de arrancar (startup)
    void onStart(@Observes StartupEvent ev) {
        log.info("Application started: Executing retroactive overdue payments routine...");
        this.executeRoutine();
    }

    // Método centralizado para evitar duplicação de código e gerir exceções
    private void executeRoutine() {
        try {
            this.paymentService.updateOverduePayments();
            log.info("Overdue payments routine finished successfully.");
        } catch (Exception e) {
            log.error("Error executing overdue payments routine: {}", e.getMessage(), e);
        }
    }
}