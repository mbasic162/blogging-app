package com.example.bloggingapp.controller;

import com.example.bloggingapp.dto.UserDto;
import com.example.bloggingapp.dto.UserFollowDto;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.mapper.UserFollowMapper;
import com.example.bloggingapp.mapper.UserMapper;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserFollowMapper userFollowMapper = UserFollowMapper.INSTANCE;

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authentication != null && authentication.isAuthenticated())
            userService.checkAllowViewingAuth(user, authentication.getName());
        else userService.checkAllowViewing(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("{username}/followers")
    public ResponseEntity<List<UserFollowDto>> getFollowers(@PathVariable String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authentication != null && authentication.isAuthenticated())
            userService.checkAllowViewingAuth(user, authentication.getName());
        else userService.checkAllowViewing(user);
        return ResponseEntity.ok(user.getFollowers().stream().map(userFollowMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("{username}/following")
    public ResponseEntity<List<UserFollowDto>> getFollowing(@PathVariable String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (authentication != null && authentication.isAuthenticated())
            userService.checkAllowViewingAuth(user, authentication.getName());
        else userService.checkAllowViewing(user);
        return ResponseEntity.ok(user.getFollowing().stream().map(userFollowMapper::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> follow(@RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userService.checkAllowViewingAuth(user, authentication.getName());
        userService.follow(user, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unfollow(
            @RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        userService.checkAllowViewingAuth(user, authentication.getName());
        userService.unfollow(user, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> block(
            @RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (userService.isBlockedByOrPrivate(user, authentication.getName())) {
            throw new UserNotFoundException("User not found!");
        }
        userService.block(user, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unblock(
            @RequestParam(name = "username") @NotBlank(message = "Please provide a username") String username, Authentication authentication) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (userService.isBlockedByOrPrivate(user, authentication.getName())) {
            throw new UserNotFoundException("User not found!");
        }
        userService.unblock(user, authentication.getName());
        return ResponseEntity.ok().build();
    }
    /*
    @GetMapping("/admintest")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("Admin access granted");
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Set<UserFollowDto>> test(Authentication authentication) {
        return ResponseEntity.ok().build();
    }
     */
}
