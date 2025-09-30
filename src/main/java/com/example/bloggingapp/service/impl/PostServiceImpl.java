package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.exception.PostNotFoundException;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.PostRepository;
import com.example.bloggingapp.service.PostService;
import com.example.bloggingapp.service.UserService;
import com.example.bloggingapp.utils.UriSanitizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Set<Post> findByUsername(String username) {
        return postRepository.findByUsername(username);
    }

    @Override
    public Set<Post> findByUsernameAuth(String username, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        return postRepository.findByUsernameAuth(username, authUser.getId());
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Set<Post> findN(int n) {
        return postRepository.findN(Limit.of(n));
    }

    @Override
    public Set<Post> findNAuth(int n, String authUsername) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        return postRepository.findNAuth(Limit.of(n), authUser);
    }

    @Override
    public void checkAllowViewing(Post post) {
        User user = post.getUser();
        if (post.getHidden() || post.getDeleted() || user.getPrivate() || user.getDeleted() || !user.getEnabled()) {
            throw new PostNotFoundException("Post not found!");
        }
    }

    @Override
    public void checkAllowViewingAuth(Post post, String authUsername) {
        User user = post.getUser();
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        if (post.getHidden() && !authUser.equals(user) || post.getDeleted() || user.getPrivate() && !user.equals(authUser) || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
            throw new PostNotFoundException("Post not found!");
        }
    }

    @Override
    public String getUriByIdAndTitle(Long postId, String title) {
        return UriSanitizer.encode(title + "-" + postId);
    }

    @Override
    public Long getIdByUri(String uri) {
        return Long.parseLong(uri.substring(uri.lastIndexOf('-') + 1));
    }

    @Override
    @Transactional
    public void like(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (post.getLikedBy().contains(authUser)) {
            throw new IllegalStateException("You already liked this post!");
        }
        if (post.getDislikedBy().contains(authUser)) {
            postRepository.removeDislike(authUser.getId(), post.getId());
            postRepository.changeRating(post, 1);
        }
        postRepository.insertLike(authUser.getId(), post.getId());
        postRepository.changeRating(post, 1);
    }

    @Override
    @Transactional
    public void removeLike(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (!post.getLikedBy().contains(authUser)) {
            throw new IllegalStateException("You haven't liked this post!");
        }
        postRepository.removeLike(authUser.getId(), post.getId());
        postRepository.changeRating(post, -1);
    }

    @Override
    @Transactional
    public void dislike(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (post.getDislikedBy().contains(authUser)) {
            throw new IllegalStateException("You already disliked this post!");
        }
        if (post.getLikedBy().contains(authUser)) {
            postRepository.removeLike(authUser.getId(), post.getId());
            postRepository.changeRating(post, -1);
        }
        postRepository.insertDislike(authUser.getId(), post.getId());
        postRepository.changeRating(post, -1);
    }


    @Override
    @Transactional
    public void removeDislike(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, authUsername);
        if (!post.getDislikedBy().contains(authUser)) {
            throw new IllegalStateException("You haven't disliked this post!");
        }
        postRepository.removeDislike(authUser.getId(), post.getId());
        postRepository.changeRating(post, 1);
    }

    @Override
    public void changeTitle(String authUsername, Long postId, String newTitle) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (!authUser.equals(post.getUser())) {
            throw new IllegalStateException("You can only change your own posts!");
        }
        if (post.getTitle().equals(newTitle)) {
            throw new IllegalArgumentException("New title must be different from the old one!");
        }
        postRepository.changeTitle(post, newTitle);
    }

    @Override
    public void changeContent(String authUsername, Long postId, String newContent) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (!authUser.equals(post.getUser())) {
            throw new IllegalStateException("You can only change your own posts!");
        }
        if (post.getContent().equals(newContent)) {
            throw new IllegalArgumentException("New content must be different from the old one!");
        }
        postRepository.changeContent(post, newContent);
    }

    @Override
    public void tempDelete(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (authUser.getBlockedUsers().contains(post.getUser()) || post.getUser().getBlockedUsers().contains(authUser) || (post.getHidden() && !post.getUser().equals(authUser))) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only delete your own posts!");
        }
        if (post.getDeleted()) {
            throw new IllegalStateException("This post is already deleted!");
        }
        postRepository.tempDelete(post);
    }

    @Override
    public void undelete(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        if (!post.getUser().equals(authUser)) {
            throw new PostNotFoundException("Post not found!");
        }
        if (!post.getDeleted()) {
            throw new IllegalStateException("This post isn't deleted!");
        }
        postRepository.undelete(post);
    }

    @Override
    public void permanentlyDelete(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (!post.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only delete your own posts!");
        }
        postRepository.delete(post);
    }

    @Override
    public void hide(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (!post.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only hide your own posts!");
        }
        if (post.getHidden()) {
            throw new IllegalStateException("This post is already hidden!");
        }
        postRepository.hide(post);
    }

    @Override
    public void unhide(String authUsername, Long postId) {
        User authUser = userService.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("Please log in again!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found!"));
        checkAllowViewingAuth(post, authUsername);
        if (!post.getUser().equals(authUser)) {
            throw new IllegalStateException("You can only unhide your own posts!");
        }
        if (!post.getHidden()) {
            throw new IllegalStateException("This post is not hidden!");
        }
        postRepository.unhide(post);
    }

}
