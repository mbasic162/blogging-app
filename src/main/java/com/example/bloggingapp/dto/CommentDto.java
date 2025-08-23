package com.example.bloggingapp.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record CommentDto(
        Long id,
        String content,
        String username,
        Integer rating,
        LocalDateTime createdAt,
        Set<CommentDto> comments
) {
}