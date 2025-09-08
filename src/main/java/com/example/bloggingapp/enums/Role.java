package com.example.bloggingapp.enums;

import lombok.Getter;

@Getter
public enum Role {
    ANONYMOUS("ROLE_ANONYMOUS"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");
    private final String role;

    Role(String role) {
        this.role = role;
    }
}
