package br.com.matteusmoreno.domain.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

@MongoEntity(collection = "motorcycles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Motorcycle {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String motorcycleId;
    private String renavam;
    private String brand;
    private String model;
    private String plate;
    private String year;
    private String color;
    private String chassis;
    private String documentUrl;
    private Boolean available;

}
