package com.example.bloggingapp.dto;

import java.time.LocalDateTime;

public record PostPreviewDto(
        Long id,
        String title,
        String username,
        Integer rating,
        Integer commentCount,
        LocalDateTime createdAt
) {
}