package com.example.bloggingapp.dto;

import java.time.LocalDate;

public record PostPreviewDto(
        Long id,
        String title,
        String username,
        String profilePicture,
        Integer rating,
        LocalDate date
) {
}