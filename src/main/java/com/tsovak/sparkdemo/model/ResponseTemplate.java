package com.tsovak.sparkdemo.model;

import lombok.Data;

@Data
public class ResponseTemplate {

    private String description;

    public ResponseTemplate(String message, Object... args) {
        this.description = String.format(message, args);
    }
}
