package com.example.bloggingapp.mapper.helper;

import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.User;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class CommentMapperHelper {
    @Named("userLiked")
    public Boolean userLiked(Comment comment, @Context User authUser) {
        return authUser != null && comment.getLikedBy().contains(authUser);
    }

    @Named("userDisliked")
    public Boolean userDisliked(Comment comment, @Context User authUser) {
        return authUser != null && comment.getDislikedBy().contains(authUser);
    }
}
