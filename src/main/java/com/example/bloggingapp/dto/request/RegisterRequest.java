package com.example.bloggingapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name cannot be blank!")
        @Pattern(regexp = "^(?!.*('|\"|;|\\|/|%|--)).*$", message = "Name cannot contain special characters!")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,
        @NotBlank(message = "Username cannot be blank!")
        @Pattern(regexp = "^(?!.*('|\"|;|\\|/|%|--| )).*$", message = "Username cannot contain special characters or spaces!")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters!")
        String username,
        @Email(message = "Invalid email format")
        @Pattern(regexp = "^(?!.*('|\"|;|\\|/|%|--| )).*$", message = "Email cannot contain special characters or spaces!")
        @Size(min = 3, max = 50, message = "Email must be between 3 and 50 characters!")
        String email,
        @NotBlank
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters!")
        String password,
        boolean isPrivate
) {
}
