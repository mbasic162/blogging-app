package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.UserDto;
import com.example.bloggingapp.mapper.helper.UserMapperHelper;
import com.example.bloggingapp.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserFollowMapper.class, PostPreviewMapper.class, CommentPreviewMapper.class, UserMapperHelper.class})
public interface UserMapper {
    @Mapping(target = "isUserBlocked", constant = "false")
    @Mapping(target = "profilePicture", source = "user", qualifiedByName = "mapProfilePicture")
    UserDto toDto(User user, @Context User authUser);
}