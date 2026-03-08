package com.example.bloggingapp.controller;

import com.example.bloggingapp.annotation.validation.Between;
import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.dto.PostPreviewDto;
import com.example.bloggingapp.dto.request.CreatePostRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.mapper.CommentMapper;
import com.example.bloggingapp.mapper.PostMapper;
import com.example.bloggingapp.mapper.PostPreviewMapper;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.CommentService;
import com.example.bloggingapp.service.PostService;
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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@Validated
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentMapper commentMapper = CommentMapper.INSTANCE;
    private final PostMapper postMapper = PostMapper.INSTANCE;
    private final PostPreviewMapper postPreviewMapper = PostPreviewMapper.INSTANCE;


    @PostMapping("/")
    public ResponseEntity<Set<PostPreviewDto>> getNPosts(
            @Between(min = 1, max = 50, message = "Number of posts must be between 1 and 50!")
            Integer numberOfPosts,
            Authentication authentication
    ) {
        String authUsername = "";
        if (authentication != null && authentication.isAuthenticated()) {
            authUsername = authentication.getName();
        }
        Set<Post> posts = postService.findN(numberOfPosts, authUsername);
        Set<PostPreviewDto> postPreviewDtos = new HashSet<>();
        for (Post post : posts) {
            postPreviewDtos.add(postPreviewMapper.toDto(post, commentService.getViewableCommentCountByPost(post, authUsername)));
        }
        return ResponseEntity.ok(postPreviewDtos);
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> createPost(
            Authentication authentication,
            @RequestBody @Valid CreatePostRequest request
    ) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postService.save(new Post(request.title(), request.content(), user, request.isHidden()));
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDto(post));
    }

    @PostMapping("/uri")
    public ResponseEntity<String> getURI(
            @NotNull Long postId,
            @NotBlank(message = "Title cannot be blank!") String title
    ) {
        return ResponseEntity.ok(postService.getURIByIdAndTitle(postId, title));
    }

    @GetMapping("/{post_uri}")
    public ResponseEntity<PostDto> getPost(
            @PathVariable(name = "post_uri")
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        String authUsername = "";
        if (authentication != null && authentication.isAuthenticated()) {
            authUsername = authentication.getName();
        }
        Post post = postService.getPostForViewByURI(postURI, authUsername);
        return ResponseEntity.ok(postMapper.toDto(post));
    }

    @GetMapping("{post_uri}/comments")
    public ResponseEntity<Set<CommentDto>> comments(
            @PathVariable(name = "post_uri")
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Set<Comment> comments;
        String authUsername = "";
        if (authentication != null && authentication.isAuthenticated()) {
            authUsername = authentication.getName();
        }
        comments = commentService.findByParentPost(postURI, authUsername);
        return ResponseEntity.ok(comments.stream().map(commentMapper::toDto).collect(Collectors.toSet()));
    }

    @PostMapping("/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> like(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.like(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeLike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeLike(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.removeLike(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> dislike(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.dislike(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removeDislike")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeDislike(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.removeDislike(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> tempDelete(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.tempDelete(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/undelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> undelete(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.undelete(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> hide(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.hide(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unhide")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unhide(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.unhide(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permanentlyDelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> permanentlyDelete(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.permanentlyDelete(authentication.getName(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeTitle")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeTitle(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication,
            @NotBlank(message = "Title cannot be blank!")
            @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters long!") String newTitle
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.changeTitle(authentication.getName(), postId, newTitle);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeContent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeContent(
            @NotBlank(message = "Post URI cannot be blank!") String postURI,
            Authentication authentication,
            @NotBlank(message = "Content cannot be blank!")
            @Size(min = 100, max = 15000, message = "Content must be between 100 and 15000 characters long!") String newContent
    ) {
        Long postId = postService.getIdByURI(postURI);
        postService.changeContent(authentication.getName(), postId, newContent);
        return ResponseEntity.ok().build();
    }
}