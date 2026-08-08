package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CommentMapper.class, UserMapper.class})
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "username", source = "post.user.username")
    @Mapping(target = "comments", source = "post.comments")
    @Mapping(target = "profilePicture", source = "post.user", qualifiedByName = "mapProfilePicture")
    @Mapping(target = "userLiked", expression = "java(userLiked(post, authUser))")
    @Mapping(target = "userDisliked", expression = "java(userDisliked(post, authUser))")
    PostDto toDto(Post post, User authUser);

    @Named("userLiked")
    default Boolean userLiked(Post post, User authUser) {
        if (authUser == null) {
            return false;
        }
        return post.getLikedBy().contains(authUser);
    }

    @Named("userDisliked")
    default Boolean userDisliked(Post post, User authUser) {
        if (authUser == null) {
            return false;
        }
        return post.getDislikedBy().contains(authUser);
    }
}