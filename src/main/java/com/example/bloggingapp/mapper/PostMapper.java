package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.mapper.helper.PostMapperHelper;
import com.example.bloggingapp.mapper.helper.UserMapperHelper;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class, PostMapperHelper.class, CommentMapper.class})
public interface PostMapper {
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "username", source = "post.user.username")
    @Mapping(target = "comments", source = "post.comments")
    @Mapping(target = "profilePicture", source = "post.user", qualifiedByName = "mapProfilePicture")
    @Mapping(target = "userLiked", source = "post", qualifiedByName = "userLiked")
    @Mapping(target = "userDisliked", source = "post", qualifiedByName = "userDisliked")
    PostDto toDto(Post post, @Context User authUser);
}