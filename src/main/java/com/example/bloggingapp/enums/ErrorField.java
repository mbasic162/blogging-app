package com.example.bloggingapp.enums;

import lombok.Getter;

@Getter
public enum ErrorField {
    USERNAME("username"),
    EMAIL("email"),
    PASSWORD("password"),
    DESCRIPTION("description"),
    PROFILE_PICTURE("profilePicture"),
    GENERAL("general");

    private final String fieldName;

    ErrorField(String fieldName) {
        this.fieldName = fieldName;
    }
}
