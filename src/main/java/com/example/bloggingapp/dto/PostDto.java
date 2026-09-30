package com.example.bloggingapp.dto;


import java.time.LocalDate;
import java.util.Set;

public record PostDto(
        Long id,
        String title,
        String content,
        String username,
        String profilePicture,
        Integer rating,
        Boolean userLiked,
        Boolean userDisliked,
        LocalDate date,
        Set<CommentDto> comments
) {
}
