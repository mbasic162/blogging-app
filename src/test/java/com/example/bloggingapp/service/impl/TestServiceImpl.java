package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.dto.UserDto;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.CommentService;
import com.example.bloggingapp.service.PostService;
import com.example.bloggingapp.service.TestService;
import com.example.bloggingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @Override
    public void checkAllowViewingPostDto(PostDto postDto, String authUsername) {
        Post post = postService.findById(postDto.id()).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = post.getUser();
        checkAllowViewingCommentDtos(postDto.comments(), authUsername);
        if (authUsername.isEmpty()) {
            if (post.getHidden() || post.getDeleted() || post.getDeletedByAdmin() || user.getPrivate() || user.getDeleted() || !user.getEnabled()) {
                throw new RuntimeException(post + "\n" + user);
            }
            return;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new RuntimeException("User not found!"));
        if (authUser.equals(user)) {
            return;
        }
        if (post.getHidden() || post.getDeleted() || post.getDeletedByAdmin() || user.getPrivate() || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
            throw new RuntimeException(post + "\n" + user);
        }
    }

    @Override
    public void checkAllowViewingPostDtos(Set<PostDto> postDtos, String authUsername) {
        Set<Post> posts = postDtos.stream().map(postDto -> postService.findById(postDto.id()).orElseThrow(() -> new RuntimeException("Post not found!"))).collect(Collectors.toSet());
        for (PostDto postDto : postDtos) {
            checkAllowViewingCommentDtos(postDto.comments(), authUsername);
        }
        if (authUsername.isEmpty()) {
            for (Post post : posts) {
                User user = post.getUser();
                if (post.getHidden() || post.getDeleted() || post.getDeletedByAdmin() || user.getPrivate() || user.getDeleted() || !user.getEnabled()) {
                    throw new RuntimeException(post + "\n" + user);
                }
            }
            return;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new RuntimeException("User not found!"));
        for (Post post : posts) {
            User user = post.getUser();
            if (!authUser.equals(user)) {
                if (post.getHidden() || post.getDeleted() || post.getDeletedByAdmin() || user.getPrivate() || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
                    throw new RuntimeException(post + "\n" + user);
                }
            }
        }
    }

    @Override
    public void checkAllowViewingUserDto(UserDto userDto, String authUsername) {
        User user = userService.findByUsername(userDto.username()).orElseThrow(() -> new RuntimeException("User not found!"));
        if (authUsername.isEmpty()) {
            if (user.getPrivate() || user.getDeleted() || !user.getEnabled()) {
                throw new RuntimeException(user.toString());
            }
            return;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new RuntimeException("User not found!"));
        if (authUser.equals(user)) {
            return;
        }
        if (user.getPrivate() || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
            throw new RuntimeException(user.toString());
        }
    }

    @Override
    public void checkAllowViewingCommentDtos(Set<CommentDto> commentDtos, String authUsername) {
        Set<Comment> comments = commentDtos.stream().map(commentDto -> commentService.findById(commentDto.id()).orElseThrow(() -> new RuntimeException("Comment not found!"))).collect(Collectors.toSet());
        for (CommentDto commentDto : commentDtos) {
            checkAllowViewingCommentDtos(commentDto.comments(), authUsername);
        }
        if (authUsername.isEmpty()) {
            for (Comment comment : comments) {
                User user = comment.getUser();
                if (comment.getHidden() || comment.getDeleted() || comment.getDeletedByAdmin() || user.getPrivate() || user.getDeleted() || !user.getEnabled()) {
                    throw new RuntimeException(comment + "\n" + user);
                }
            }
            return;
        }
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new RuntimeException("User not found!"));
        for (Comment comment : comments) {
            User user = comment.getUser();
            if (!authUser.equals(user)) {
                if (comment.getHidden() || comment.getDeleted() || comment.getDeletedByAdmin() || user.getPrivate() || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
                    throw new RuntimeException(comment + "\n" + user);
                }
            }
        }
    }
}
