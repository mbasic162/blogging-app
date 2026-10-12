package com.example.bloggingapp.service.helper;

import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import com.example.bloggingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViewHelper {
    private final UserRepository userRepository;


    public boolean isUserViewable(User user) {
        return !user.getPrivate() && !user.getDeleted() && user.getEnabled();
    }

    public boolean isUserViewable(User user, User authUser) {
        if (authUser.equals(user)) {
            return true;
        }
        return !user.getPrivate() && !user.getDeleted() && user.getEnabled() && !userRepository.isUserBlocked(authUser, user) && !userRepository.isUserBlocked(user, authUser);
    }

    public boolean isPostViewable(Post post) {
        User user = post.getUser();
        return !post.getHidden() && !post.getDeleted() && !post.getDeletedByAdmin() && isUserViewable(user);
    }

    public boolean isPostViewable(Post post, User authUser) {
        User user = post.getUser();
        if (authUser.equals(user)) {
            return true;
        }
        return !post.getHidden() && !post.getDeleted() && !post.getDeletedByAdmin() && isUserViewable(user, authUser);
    }

    public boolean isCommentViewable(Comment comment) {
        User user = comment.getUser();
        return !comment.getHidden() && !comment.getDeleted() && !comment.getDeletedByAdmin() && isUserViewable(user) && isPostViewable(comment.getParentPost());
    }

    public boolean isCommentViewable(Comment comment, User authUser) {
        User user = comment.getUser();
        return (user.equals(authUser) || !comment.getHidden() && !comment.getDeleted() && !comment.getDeletedByAdmin()) && isUserViewable(user, authUser) && isPostViewable(comment.getParentPost(), authUser);
    }

    public void filterComments(Collection<Comment> comments) {
        for (Iterator<Comment> i = comments.iterator(); i.hasNext(); ) {
            Comment comment = i.next();
            if (!isCommentViewable(comment)) {
                i.remove();
            } else {
                filterComments(comment.getComments());
            }
        }
    }

    public void filterComments(Collection<Comment> comments, User authUser) {
        for (Iterator<Comment> i = comments.iterator(); i.hasNext(); ) {
            Comment comment = i.next();
            if (!isCommentViewable(comment, authUser)) {
                i.remove();
            } else {
                filterComments(comment.getComments(), authUser);
            }
        }
    }

    public void filterPosts(Collection<Post> posts) {
        posts.removeIf(post -> !isPostViewable(post));
    }

    public void filterPosts(Collection<Post> posts, User authUser) {
        posts.removeIf(post -> !isPostViewable(post, authUser));
    }

    public void filterPostContent(Post post) {
        Set<Comment> comments = post.getComments();
        filterComments(comments);
        post.setComments(comments);
    }

    public void filterPostContent(Post post, User authUser) {
        Set<Comment> comments = post.getComments();
        filterComments(comments, authUser);
        post.setComments(comments);
    }

    public void filterUsers(Collection<User> users) {
        users.removeIf(user -> !isUserViewable(user));
    }

    public void filterUsers(Collection<User> users, User authUser) {
        users.removeIf(user -> !isUserViewable(user, authUser));
    }

    public void filterUserContent(User user) {
        Set<Post> posts = user.getPosts();
        Set<Comment> comments = user.getComments();
        Set<User> followers = user.getFollowers();
        Set<User> following = user.getFollowing();
        filterPosts(posts);
        filterComments(comments);
        filterUsers(followers);
        filterUsers(following);
        user.setPosts(posts);
        user.setComments(comments);
        user.setFollowers(followers);
        user.setFollowing(following);
    }

    public void filterUserContent(User user, User authUser) {
        Set<Post> posts = user.getPosts();
        Set<Comment> comments = user.getComments();
        Set<User> followers = user.getFollowers();
        Set<User> following = user.getFollowing();
        filterPosts(posts, authUser);
        filterComments(comments, authUser);
        filterUsers(followers, authUser);
        filterUsers(following, authUser);
        user.setPosts(posts);
        user.setComments(comments);
        user.setFollowers(followers);
        user.setFollowing(following);
    }
}
