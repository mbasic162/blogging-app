package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CommentMapper.class})
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "username", source = "user.username")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);
}
