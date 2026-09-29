package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.EditProfileDto;
import com.example.bloggingapp.mapper.helper.UserMapperHelper;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class})
public interface EditProfileMapper {
    @Mapping(target = "profilePicture", source = "user", qualifiedByName = "mapProfilePicture")
    @Mapping(target = "isPrivate", source = "private")
    @Mapping(target = "isDeleted", source = "deleted")
    EditProfileDto toDto(User user);
}