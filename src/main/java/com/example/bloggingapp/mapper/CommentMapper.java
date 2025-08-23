package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "username", source = "user.username")
    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto commentDto);
}