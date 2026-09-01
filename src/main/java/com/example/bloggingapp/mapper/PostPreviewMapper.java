package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.PostPreviewDto;
import com.example.bloggingapp.mapper.helper.GeneralMapperHelper;
import com.example.bloggingapp.mapper.helper.UserMapperHelper;
import com.example.bloggingapp.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class, GeneralMapperHelper.class})
public interface PostPreviewMapper {
    @Mapping(target = "username", source = "post.user.username")
    @Mapping(target = "date", source = "post.createdAt", qualifiedByName = "localDateTimeToLocalDate")
    @Mapping(target = "profilePicture", source = "post.user", qualifiedByName = "mapProfilePicture")
    PostPreviewDto toDto(Post post);

}