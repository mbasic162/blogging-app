package com.example.bloggingapp.mapper.helper;

import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;


@Component
public class PostMapperHelper {
    @Named("userLiked")
    public Boolean userLiked(Post post, @Context User authUser) {
        return authUser != null && post.getLikedBy().contains(authUser);
    }

    @Named("userDisliked")
    public Boolean userDisliked(Post post, @Context User authUser) {
        return authUser != null && post.getDislikedBy().contains(authUser);
    }
}
