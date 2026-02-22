package br.com.matteusmoreno.domain.entity;

import br.com.matteusmoreno.domain.constant.PaymentMethod;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.constant.PaymentType;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MongoEntity(collection = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Payment {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String paymentId;

    private String contractId;

    private PaymentType type;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private PaymentStatus status;
    private PaymentMethod method;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

