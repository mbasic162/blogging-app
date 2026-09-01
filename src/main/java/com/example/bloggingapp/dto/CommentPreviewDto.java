package com.example.bloggingapp.dto;

import java.time.LocalDate;

public record CommentPreviewDto(
        Long id,
        String content,
        String username,
        Integer rating,
        String profilePicture,
        Boolean userLiked,
        Boolean userDisliked,
        LocalDate date
) {
}