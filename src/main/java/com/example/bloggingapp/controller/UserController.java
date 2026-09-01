package com.example.bloggingapp.controller;

import com.example.bloggingapp.dto.CommentDto;
import com.example.bloggingapp.dto.PostPreviewDto;
import com.example.bloggingapp.dto.UserDto;
import com.example.bloggingapp.dto.UserFollowDto;
import com.example.bloggingapp.dto.request.EmailChangeRequest;
import com.example.bloggingapp.dto.request.PasswordChangeRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.mapper.CommentMapper;
import com.example.bloggingapp.mapper.PostPreviewMapper;
import com.example.bloggingapp.mapper.UserFollowMapper;
import com.example.bloggingapp.mapper.UserMapper;
import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.CommentService;
import com.example.bloggingapp.service.PostService;
import com.example.bloggingapp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserFollowMapper userFollowMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final PostPreviewMapper postPreviewMapper;
    private final CommentMapper commentMapper;

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        User authUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            authUser = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        }
        User user = userService.getUserForViewByUsername(username, authUser);
        return ResponseEntity.ok(userMapper.toDto(user, authUser));
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<Set<PostPreviewDto>> getPosts(
            @PathVariable
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        User authUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            authUser = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        }
        Set<Post> posts = postService.findByUsername(username, authUser);
        return ResponseEntity.ok(posts.stream().map(postPreviewMapper::toDto).collect(Collectors.toSet()));
    }

    @GetMapping("/{username}/comments")
    public ResponseEntity<Set<CommentDto>> getComments(
            @PathVariable
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        User authUser;
        if (authentication != null && authentication.isAuthenticated()) {
            authUser = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        } else {
            authUser = null;
        }
        Set<Comment> comments = commentService.findByUser(user, authUser);
        return ResponseEntity.ok(comments.stream().map(comment -> commentMapper.toDto(comment, authUser)).collect(Collectors.toSet()));
    }

    @GetMapping("{username}/followers")
    public ResponseEntity<Set<UserFollowDto>> getFollowers(
            @PathVariable
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        User authUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            authUser = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        }
        Set<User> followers = userService.findFollowers(username, authUser);
        return ResponseEntity.ok(followers.stream().map(userFollowMapper::toDto).collect(Collectors.toSet()));
    }

    @GetMapping("{username}/following")
    public ResponseEntity<Set<UserFollowDto>> getFollowing(
            @PathVariable
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        User authUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            authUser = userService.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        }
        Set<User> following = userService.findFollowing(username, authUser);
        return ResponseEntity.ok(following.stream().map(userFollowMapper::toDto).collect(Collectors.toSet()));
    }

    @PostMapping("/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> follow(
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        userService.follow(username, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unfollow(
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        userService.unfollow(username, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> block(
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        userService.block(username, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unblock(
            @NotBlank(message = "Username cannot be blank!") String username,
            Authentication authentication
    ) {
        userService.unblock(username, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> tempDelete(
            Authentication authentication
    ) {
        userService.tempDelete(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/undelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> undelete(
            Authentication authentication
    ) {
        userService.undelete(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permanentlyDelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> permanentlyDelete(
            @NotBlank(message = "Password cannot be blank!") String password,
            Authentication authentication
    ) {
        userService.permanentlyDelete(authentication.getName(), password);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeUsername")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changeUsername(
            @NotBlank(message = "Username cannot be blank!")
            @Pattern(regexp = "^(?!.*('|\"|;|\\|/|%|--| )).*$", message = "Username cannot contain special characters or spaces!")
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters!") String newUsername,
            Authentication authentication
    ) {
        String newToken = userService.changeUsername(newUsername, authentication.getName());
        return ResponseEntity.ok().body(newToken);
    }

    @PostMapping("/changeEmail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeEmail(
            @RequestBody @Valid EmailChangeRequest request,
            Authentication authentication
    ) {
        userService.changeEmail(request, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changePassword")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid PasswordChangeRequest request,
            Authentication authentication
    ) {
        userService.changePassword(request, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeDescription")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeDescription(
            @Size(max = 200, message = "Description must be at most 200 characters!") String newDescription,
            Authentication authentication
    ) {
        userService.changeDescription(newDescription, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changeProfilePicture")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeProfilePicture(
            MultipartFile newProfilePicture,
            Authentication authentication
    ) {
        userService.changeProfilePicture(newProfilePicture, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/goPrivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> goPrivate(
            Authentication authentication
    ) {
        userService.goPrivate(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/goPublic")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> goPublic(
            Authentication authentication
    ) {
        userService.goPublic(authentication.getName());
        return ResponseEntity.ok().build();
    }
}