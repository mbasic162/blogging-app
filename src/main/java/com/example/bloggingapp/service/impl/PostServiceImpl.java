package com.example.bloggingapp.service.impl;

import com.example.bloggingapp.exception.PostNotFoundException;
import com.example.bloggingapp.exception.UserNotFoundException;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.PostRepository;
import com.example.bloggingapp.repository.UserRepository;
import com.example.bloggingapp.service.PostService;
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
    private final UserRepository userRepository;

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Set<Post> findByUsername(String username) {
        return postRepository.findByUsername(username);
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
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return postRepository.findNAuth(Limit.of(n), authUser.getId());
    }

    @Override
    public Optional<String> getTitleById(Long id) {
        return postRepository.getTitleById(id);
    }

    @Override
    public void checkAllowViewing(Post post) {
        User user = post.getUser();
        if (post.getHidden() || post.getDeleted() || (user.getPrivate() && !post.getShareableDespitePrivateUser())) {
            throw new PostNotFoundException("Post not found!");
        }
    }

    @Override
    public void checkAllowViewingAuth(Post post, String authUsername) {
        User user = post.getUser();
        User authUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (post.getHidden() && !authUser.equals(user) || post.getDeleted() || user.getPrivate() && !post.getShareableDespitePrivateUser() && !user.equals(authUser) || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)) {
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
    public void like(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, username);
        if (post.getLikedBy().contains(user)) throw new IllegalStateException("You already liked this post!");
        if (post.getDislikedBy().contains(user)) removeDislike(user, post);
        postRepository.insertLike(user.getId(), post.getId());
        postRepository.changeRating(post.getId(), 1);
    }

    @Override
    @Transactional
    public void removeLike(User user, Post post) {
        postRepository.removeLike(user.getId(), post.getId());
        postRepository.changeRating(post.getId(), -1);
    }

    @Override
    @Transactional
    public void removeLike(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, username);
        if (!post.getLikedBy().contains(user)) throw new IllegalStateException("You haven't liked this post yet!");
        postRepository.removeLike(user.getId(), post.getId());
        postRepository.changeRating(post.getId(), -1);
    }

    @Override
    @Transactional
    public void dislike(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, username);
        if (post.getDislikedBy().contains(user)) throw new IllegalStateException("You already disliked this post!");
        if (post.getLikedBy().contains(user)) removeLike(user, post);
        postRepository.insertDislike(user.getId(), post.getId());
        postRepository.changeRating(post.getId(), -1);
    }

    @Override
    @Transactional
    public void removeDislike(User user, Post post) {
        postRepository.removeDislike(user.getId(), post.getId());
        postRepository.changeRating(post.getId(), 1);
    }

    @Override
    @Transactional
    public void removeDislike(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, username);
        if (!post.getDislikedBy().contains(user)) throw new IllegalStateException("You haven't disliked this post!");
        postRepository.removeDislike(user.getId(), post.getId());
        postRepository.changeRating(post.getId(), 1);
    }

    @Override
    public void tempDelete(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        if (user.getBlockedUsers().contains(post.getUser()) || post.getUser().getBlockedUsers().contains(user))
            throw new PostNotFoundException("Post not found!");
        if (!post.getUser().equals(user)) throw new IllegalStateException("You can only delete your own posts!");
        if (post.getDeleted()) throw new IllegalStateException("This post is already deleted!");
        postRepository.tempDelete(post.getId());
    }

    @Override
    public void undelete(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        if (user.getBlockedUsers().contains(post.getUser()) || post.getUser().getBlockedUsers().contains(user))
            throw new PostNotFoundException("Post not found!");
        if (!post.getUser().equals(user)) throw new PostNotFoundException("Post not found!");
        if (!post.getDeleted()) throw new IllegalStateException("This post isn't deleted!");
        postRepository.undelete(post.getId());
    }

    @Override
    public void permanentlyDelete(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, username);
        if (!post.getUser().equals(user)) throw new IllegalStateException("You can only delete your own posts!");
        postRepository.delete(post);
    }

    @Override
    public void hide(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, username);
        if (!post.getUser().equals(user)) throw new IllegalStateException("You can only hide your own posts!");
        if (post.getHidden()) throw new IllegalStateException("This post is already hidden!");
        postRepository.hide(post.getId());
    }

    @Override
    public void unhide(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Invalid post!"));
        checkAllowViewingAuth(post, username);
        if (!post.getUser().equals(user)) throw new IllegalStateException("You can only unhide your own posts!");
        if (!post.getHidden()) throw new IllegalStateException("This post is not hidden!");
        postRepository.unhide(post.getId());
    }

}
