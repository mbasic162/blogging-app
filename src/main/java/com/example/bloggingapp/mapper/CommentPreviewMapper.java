package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.CommentPreviewDto;
import com.example.bloggingapp.mapper.helper.CommentMapperHelper;
import com.example.bloggingapp.mapper.helper.GeneralMapperHelper;
import com.example.bloggingapp.mapper.helper.UserMapperHelper;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class, GeneralMapperHelper.class, CommentMapperHelper.class})
public interface CommentPreviewMapper {
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "profilePicture", source = "user", qualifiedByName = "mapProfilePicture")
    @Mapping(target = "date", source = "createdAt", qualifiedByName = "localDateTimeToLocalDate")
    @Mapping(target = "userLiked", source = "comment", qualifiedByName = "userLiked")
    @Mapping(target = "userDisliked", source = "comment", qualifiedByName = "userDisliked")
    CommentPreviewDto toDto(Comment comment, @Context User authUser);
}