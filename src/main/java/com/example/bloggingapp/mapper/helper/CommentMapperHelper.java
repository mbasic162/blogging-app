package com.example.bloggingapp.mapper.helper;

import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapperHelper {
    private final CommentService commentService;

    @Named("userLiked")
    public Boolean userLiked(Comment comment, @Context User authUser) {
        return authUser != null && commentService.isLikedByUser(comment, authUser);
    }

    @Named("userDisliked")
    public Boolean userDisliked(Comment comment, @Context User authUser) {
        return authUser != null && commentService.isDislikedByUser(comment, authUser);
    }
}
