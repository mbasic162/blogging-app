package com.example.bloggingapp.mapper.helper;

import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PostMapperHelper {
    private final PostService postService;


    @Named("userLiked")
    public Boolean userLiked(Post post, @Context User authUser) {
        return authUser != null && postService.isLikedByUser(post, authUser);
    }

    @Named("userDisliked")
    public Boolean userDisliked(Post post, @Context User authUser) {
        return authUser != null && postService.isDislikedByUser(post, authUser);
    }
}
