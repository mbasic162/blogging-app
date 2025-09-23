package com.example.bloggingapp.controller;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.request.CreateCommentRequest;
import com.example.bloggingapp.exception.CommentNotFoundException;
import com.example.bloggingapp.mapper.CommentMapper;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<CommentDto> createComment(Authentication authentication, @RequestBody @Valid CreateCommentRequest request) {
        Comment comment = commentService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toDto(comment));
    }

    @PostMapping("/uri")
    public ResponseEntity<String> getUri(@RequestParam(name = "commentId") @NotNull Long commentId, @RequestParam(name = "content") @NotNull String content, Authentication authentication) {
        Comment comment = commentService.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        String contentById = comment.getContent();
        if (!contentById.equals(content)) {
            return ResponseEntity.notFound().build();
        }
        if (authentication != null && authentication.isAuthenticated()) {
            commentService.checkAllowViewingAuth(comment, authentication.getName());
        } else {
            commentService.checkAllowViewing(comment);
        }
        return ResponseEntity.ok(commentService.getUriByIdAndContent(commentId, content));
    }

    @GetMapping("/{comment_uri}")
    public ResponseEntity<CommentDto> getComment(@PathVariable(name = "comment_uri") @NotNull String commentUri, Authentication authentication) {
        Comment comment = commentService.findById(commentService.getIdByUri(commentUri)).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        if (!commentService.getUriByIdAndContent(comment.getId(), comment.getContent()).equals(commentUri)) {
            throw new CommentNotFoundException("Comment not found!");
        }
        if (authentication != null && authentication.isAuthenticated()) {
            commentService.checkAllowViewingAuth(comment, authentication.getName());
        } else {
            commentService.checkAllowViewing(comment);
        }
        return ResponseEntity.ok(commentMapper.toDto(comment));
    }

    @GetMapping("/{comment_uri}/comments")
    public ResponseEntity<Set<CommentDto>> comments(@PathVariable(name = "comment_uri") String commentUri, Authentication authentication) {
        Comment comment = commentService.findById(commentService.getIdByUri(commentUri)).orElseThrow(() -> new CommentNotFoundException("Comment not found!"));
        Set<Comment> comments;
        if (authentication != null && authentication.isAuthenticated()) {
            commentService.checkAllowViewingAuth(comment, authentication.getName());
            comments = commentService.findByParentCommentAuth(comment, authentication.getName());
        } else {
            comments = commentService.findByParentComment(comment);
            commentService.checkAllowViewing(comment);
        }
        return ResponseEntity.ok(comments.stream().map(commentMapper::toDto).collect(Collectors.toSet()));
    }

    @PostMapping("/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> like(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.like(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeLike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeLike(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.removeLike(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> dislike(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.dislike(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeDislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeDislike(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.removeDislike(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> tempDelete(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.tempDelete(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/undelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> undelete(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.undelete(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> hide(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.hide(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unhide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unhide(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.unhide(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permanentlyDelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> permanentlyDelete(Authentication authentication, @RequestParam(name = "commentId") @NotNull Long commentId) {
        commentService.permanentlyDelete(authentication.getName(), commentId);
        return ResponseEntity.ok().build();
    }
}