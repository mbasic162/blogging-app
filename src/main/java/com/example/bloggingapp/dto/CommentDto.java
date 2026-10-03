package com.example.bloggingapp.dto;

import java.time.LocalDate;
import java.util.Set;

public record CommentDto(
        Long id,
        String content,
        String username,
        Integer rating,
        String profilePicture,
        Boolean userLiked,
        Boolean userDisliked,
        Boolean isHidden,
        LocalDate date,
        Set<CommentDto> comments
) {
}