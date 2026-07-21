package com.example.bloggingapp.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;


public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank!")
        @Pattern(regexp = "^\\S*$", message = "Username cannot contain spaces!")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters!")
        String username,
        @Email(message = "Invalid email format")
        @Size(min = 3, max = 50, message = "Email must be between 3 and 50 characters!")
        String email,
        @NotNull(message = "Password cannot be empty!")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters!")
        String password,
        @Size(max = 200, message = "Description must be at most 200 characters!")
        String description,
        MultipartFile profilePicture,
        boolean isPrivate
) {
}
