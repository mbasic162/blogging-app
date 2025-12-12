package com.example.bloggingapp.mapper;

import com.example.bloggingapp.dto.PostPreviewDto;
import com.example.bloggingapp.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostPreviewMapper {
    PostPreviewMapper INSTANCE = Mappers.getMapper(PostPreviewMapper.class);

    @Mapping(target = "username", source = "post.user.username")
    @Mapping(target = "commentCount", source = "commentCount")
    PostPreviewDto toDto(Post post, Integer commentCount);

    Post toEntity(PostPreviewDto postPreviewDto);
}