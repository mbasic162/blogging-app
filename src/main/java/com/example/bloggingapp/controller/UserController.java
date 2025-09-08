package com.example.bloggingapp.controller;

import com.example.bloggingapp.dto.PostDto;
import com.example.bloggingapp.dto.UserDto;
import com.example.bloggingapp.dto.UserFollowDto;
import com.example.bloggingapp.dto.request.LoginRequest;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.mapper.PostMapper;
import com.example.bloggingapp.mapper.UserFollowMapper;
import com.example.bloggingapp.mapper.UserMapper;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.PostService;
import com.example.bloggingapp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserFollowMapper userFollowMapper = UserFollowMapper.INSTANCE;
    private final PostService postService;
    private final PostMapper postMapper = PostMapper.INSTANCE;


    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authentication != null && authentication.isAuthenticated()) {
            userService.checkAllowViewingAuth(user, authentication.getName());
        } else {
            userService.checkAllowViewing(user);
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<Set<PostDto>> getPosts(@PathVariable String username, Authentication authentication) {
        if (!userService.existsByUsername(username)) {
            throw new UserNotFoundException("User not found!");
        }
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(postService.findByUsernameAuth(username, authentication.getName()).stream().map(postMapper::toDto).collect(Collectors.toSet()));
        }
        return ResponseEntity.ok(postService.findByUsername(username).stream().map(postMapper::toDto).collect(Collectors.toSet()));
    }

    @GetMapping("{username}/followers")
    public ResponseEntity<Set<UserFollowDto>> getFollowers(@PathVariable String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authentication != null && authentication.isAuthenticated()) {
            userService.checkAllowViewingAuth(user, authentication.getName());
        } else {
            userService.checkAllowViewing(user);
        }
        return ResponseEntity.ok(user.getFollowers().stream().map(userFollowMapper::toDto).collect(Collectors.toSet()));
    }

    @GetMapping("{username}/following")
    public ResponseEntity<Set<UserFollowDto>> getFollowing(@PathVariable String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authentication != null && authentication.isAuthenticated()) {
            userService.checkAllowViewingAuth(user, authentication.getName());
        } else {
            userService.checkAllowViewing(user);
        }
        return ResponseEntity.ok(user.getFollowing().stream().map(userFollowMapper::toDto).collect(Collectors.toSet()));
    }

    @PostMapping("/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> follow(@RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userService.checkAllowViewingAuth(user, authentication.getName());
        userService.follow(user, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unfollow(
            @RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userService.checkAllowViewingAuth(user, authentication.getName());
        userService.unfollow(user, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> block(
            @RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userService.block(user, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unblock(
            @RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userService.unblock(user, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> tempDelete(
            Authentication authentication) {
        userService.tempDelete(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/undelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> undelete(
            Authentication authentication) {
        userService.undelete(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permanentlyDelete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> permanentlyDelete(
            Authentication authentication, @RequestBody @Valid LoginRequest request) {
        userService.permanentlyDelete(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }
}
