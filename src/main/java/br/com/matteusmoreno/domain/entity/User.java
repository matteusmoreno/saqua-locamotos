package br.com.matteusmoreno.domain.entity;

import br.com.matteusmoreno.domain.constant.MaritalStatus;
import br.com.matteusmoreno.domain.constant.UserRole;
import br.com.matteusmoreno.domain.model.Address;
import br.com.matteusmoreno.domain.model.UserDocument;
import br.com.matteusmoreno.domain.model.ErrorInfo;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class User {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String userId;
    private String name;
    private String email;
    private Boolean emailVerified;
    private String emailVerificationToken;
    private String passwordResetToken;
    private String password;
    private String phone;
    private String cpf;
    private String rg;
    private String occupation;
    private MaritalStatus maritalStatus;
    private Address address;
    @Builder.Default
    private List<ErrorInfo> errors = new ArrayList<>();
    private String pictureUrl;
    private UserDocument documents;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
