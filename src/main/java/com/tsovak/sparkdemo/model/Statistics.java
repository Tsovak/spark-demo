package com.tsovak.sparkdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Statistics implements Serializable {

    @Id
    UUID uuid;
    UUID userUuid;
    @Indexed
    Long activity;
    LocalDateTime date;
}
