package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.PostPreviewDto;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(uses = {UserMapper.class})
public interface PostPreviewMapper {
    PostPreviewMapper INSTANCE = Mappers.getMapper(PostPreviewMapper.class);

    @Mapping(target = "username", source = "post.user.username")
    @Mapping(target = "date", source = "post.createdAt", qualifiedByName = "localDateTimeToLocalDate")
    @Mapping(target = "profilePicture", source = "post.user", qualifiedByName = "mapProfilePicture")
    PostPreviewDto toDto(Post post);

    @Named("localDateTimeToLocalDate")
    default LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }
}