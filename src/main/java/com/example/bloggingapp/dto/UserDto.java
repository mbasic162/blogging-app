package com.example.bloggingapp.dto;

import java.util.Set;

public record UserDto(
        String username,
        String profilePicture,
        String description,
        Set<UserFollowDto> followers,
        Set<UserFollowDto> following,
        Set<PostPreviewDto> posts,
        Set<CommentPreviewDto> comments,
        boolean isUserBlocked
) {
}