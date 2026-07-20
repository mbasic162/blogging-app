package com.example.bloggingapp.exception;

import lombok.Getter;

@Getter
public class FormException extends RuntimeException {
    String field;

    public FormException(String field, String message) {
        super(message);
        this.field = field;
    }
}
