package com.example.bloggingapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailChangeRequest(
        @Email(message = "Invalid email format")
        @Size(min = 3, max = 50, message = "Email must be between 3 and 50 characters!")
        String newEmail,
        @NotBlank
        String password
) {
}
