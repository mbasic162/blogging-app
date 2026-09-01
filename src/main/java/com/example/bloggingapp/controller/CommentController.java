package com.example.bloggingapp.controller;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.mapper.CommentMapper;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.CommentService;
import com.example.bloggingapp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
@Validated
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> createComment(
            @RequestBody @Valid CreateCommentRequest request,
            Authentication authentication
    ) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Comment comment = commentService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toDto(comment, user));
    }

    @PostMapping("/uri")
    public ResponseEntity<String> getURI(
            @NotNull Long commentId,
            @NotBlank(message = "Content cannot be blank!") String content) {
        return ResponseEntity.ok(commentService.getURIByIdAndContent(commentId, content));
    }

    @GetMapping("/{comment_uri}")
    public ResponseEntity<CommentDto> getComment(
            @PathVariable(name = "comment_uri")
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication) {
        User authUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            authUser = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        }
        Comment comment = commentService.getCommentForViewByURI(commentURI, authUser);
        return ResponseEntity.ok(commentMapper.toDto(comment, authUser));
    }

    @GetMapping("/{comment_uri}/comments")
    public ResponseEntity<Set<CommentDto>> comments(
            @PathVariable(name = "comment_uri")
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        User authUser;
        if (authentication != null && authentication.isAuthenticated()) {
            authUser = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        } else {
            authUser = null;
        }
        Set<Comment> comments = commentService.findByParentComment(commentURI, authUser);
        return ResponseEntity.ok(comments.stream().map(comment -> commentMapper.toDto(comment, authUser)).collect(Collectors.toSet()));
    }

    @PostMapping("/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> like(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.like(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeLike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeLike(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.removeLike(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> dislike(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.dislike(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeDislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeDislike(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.removeDislike(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> tempDelete(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.tempDelete(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/undelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> undelete(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.undelete(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> hide(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.hide(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unhide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unhide(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.unhide(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeContent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeContent(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication,
            @NotBlank(message = "Content cannot be blank!")
            @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters!") String newContent
    ) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.changeContent(commentId, authentication.getName(), newContent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permanentlyDelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> permanentlyDelete(
            @NotBlank(message = "Comment URI cannot be blank!") String commentURI,
            Authentication authentication) {
        Long commentId = commentService.getIdByURI(commentURI);
        commentService.permanentlyDelete(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}