package com.example.bloggingapp.controller;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.exception.CommentNotFoundException;
import com.example.bloggingapp.mapper.CommentMapper;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.service.CommentService;
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
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> createComment(
            @RequestBody @Valid CreateCommentRequest request,
            Authentication authentication
    ) {
        Comment comment = commentService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toDto(comment));
    }

    @PostMapping("/uri")
    public ResponseEntity<String> getUri(
            @NotNull Long commentId,
            @NotBlank(message = "Content cannot be blank!") String content,
            Authentication authentication) {
        Comment comment = commentService.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        String contentById = comment.getContent();
        String authUsername = "";
        if (authentication != null && authentication.isAuthenticated()) {
            authUsername = authentication.getName();
        }
        if (!commentService.isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!contentById.equals(content)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        return ResponseEntity.ok(commentService.getUriByIdAndContent(commentId, content));
    }

    @GetMapping("/{comment_uri}")
    public ResponseEntity<CommentDto> getComment(
            @PathVariable(name = "comment_uri")
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication) {
        Comment comment = commentService.findById(commentService.getIdByUri(commentUri)).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        String authUsername = "";
        if (authentication != null && authentication.isAuthenticated()) {
            authUsername = authentication.getName();
        }
        if (!commentService.isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (!commentService.getUriByIdAndContent(comment.getId(), comment.getContent()).equalsIgnoreCase(commentUri)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        return ResponseEntity.ok(commentMapper.toDto(comment));
    }

    @GetMapping("/{comment_uri}/comments")
    public ResponseEntity<Set<CommentDto>> comments(
            @PathVariable(name = "comment_uri")
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Comment comment = commentService.findById(commentService.getIdByUri(commentUri)).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        String authUsername = "";
        if (authentication != null && authentication.isAuthenticated()) {
            authUsername = authentication.getName();
        }
        if (!commentService.isViewable(comment, authUsername)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        Set<Comment> comments = commentService.findByParentComment(comment, authUsername);
        return ResponseEntity.ok(comments.stream().map(commentMapper::toDto).collect(Collectors.toSet()));
    }

    @PostMapping("/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> like(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.like(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeLike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeLike(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.removeLike(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> dislike(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.dislike(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeDislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeDislike(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.removeDislike(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> tempDelete(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.tempDelete(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/undelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> undelete(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.undelete(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> hide(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.hide(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unhide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unhide(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.unhide(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeContent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeContent(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication,
            @NotBlank(message = "Content cannot be blank!")
            @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters!") String newContent
    ) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.changeContent(authentication.getName(), commentId, newContent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permanentlyDelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> permanentlyDelete(
            @NotBlank(message = "Comment uri cannot be blank!") String commentUri,
            Authentication authentication) {
        Long commentId = commentService.getIdByUri(commentUri);
        commentService.permanentlyDelete(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }
}