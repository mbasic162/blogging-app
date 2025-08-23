package com.example.bloggingapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        Long parentPostId,
        Long parentCommentId,
        @NotBlank(message = "Content cannot be blank!")
        @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters!")
        String content
) {
}
