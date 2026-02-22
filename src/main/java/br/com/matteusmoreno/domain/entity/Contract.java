package br.com.matteusmoreno.domain.entity;

import br.com.matteusmoreno.domain.constant.ContractStatus;
import br.com.matteusmoreno.domain.constant.RentalType;
import br.com.matteusmoreno.domain.model.Fine;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "contracts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Contract {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String contractId;

    private User user;
    private Motorcycle motorcycle;

    private RentalType rentalType;
    private ContractStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal depositAmount;
    private Boolean depositPaid;
    private Boolean depositRefunded;

    private BigDecimal weeklyAmount;
    private BigDecimal totalAmount;

    private String contractUrl;

    @Builder.Default
    private List<String> paymentIds = new ArrayList<>();

    @Builder.Default
    private List<Fine> fines = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
