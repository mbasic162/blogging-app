package com.example.bloggingapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank!")
        @Pattern(regexp = "^(?!.*('|\"|;|\\|/|%|--| )).*$", message = "Username cannot contain special characters or spaces!")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters!")
        String username,
        @Email(message = "Invalid email format")
        @Pattern(regexp = "^(?!.*('|\"|;|\\|/|%|--| )).*$", message = "Email cannot contain special characters or spaces!")
        @Size(min = 3, max = 50, message = "Email must be between 3 and 50 characters!")
        String email,
        @NotBlank(message = "Password cannot be blank!")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters!")
        String password,
        @Size(max = 200, message = "Description must be at most 200 characters!")
        String description,
        boolean isPrivate
) {
}
