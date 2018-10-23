package com.tsovak.sparkdemo.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "users")
public class User {

    @Id
    UUID uuid;
    String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime registrationDate;

    @DBRef(lazy = true)
    @ToString.Exclude
    transient List<Statistics> statistics = new ArrayList<>();

    public User(UUID uuid, String name, LocalDateTime registrationDate) {
        this.uuid = uuid;
        this.name = name;
        this.registrationDate = registrationDate;
    }

    public static User create(String name) {
        return new User(UUID.randomUUID(), name, LocalDateTime.now());
    }
}
