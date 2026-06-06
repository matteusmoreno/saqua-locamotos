package br.com.matteusmoreno.domain.repository;

import br.com.matteusmoreno.application.exception.PaymentNotFoundException;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.entity.Payment;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PaymentRepository implements PanacheMongoRepositoryBase<Payment, String> {

    public Payment findPaymentById(String paymentId) {
        if (!ObjectId.isValid(paymentId)) throw new PaymentNotFoundException();

        return find("_id", new ObjectId(paymentId)).firstResultOptional()
                .orElseThrow(PaymentNotFoundException::new);
    }

    public List<Payment> findPaymentsByContractId(String contractId) {
        if (!ObjectId.isValid(contractId)) return List.of();
        return find("contractId", contractId).list();
    }

    public List<Payment> findPendingAndOverdue(LocalDate today) {
        return find("status = ?1 and dueDate < ?2", PaymentStatus.PENDING, today).list();
    }
}

