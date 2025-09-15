package com.example.bloggingapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank(message = "Password cannot be blank!")
        String password,
        @NotBlank(message = "New password cannot be blank!")
        @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters!")
        String newPassword
) {
}
