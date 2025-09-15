package com.example.bloggingapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "Title cannot be blank!")
        @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters long!")
        String title,
        @NotBlank(message = "Content cannot be blank!")
        @Size(min = 100, max = 15000, message = "Content must be between 100 and 15000 characters long!")
        String content,
        @NotNull
        Boolean isHidden
) {
}
