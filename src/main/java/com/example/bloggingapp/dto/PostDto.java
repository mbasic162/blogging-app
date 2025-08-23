package com.example.bloggingapp.dto;


import java.time.LocalDateTime;
import java.util.Set;

public record PostDto(
        Long id,
        String title,
        String content,
        String username,
        Integer rating,
        LocalDateTime createdAt,
        Set<CommentDto> comments
) {
}
