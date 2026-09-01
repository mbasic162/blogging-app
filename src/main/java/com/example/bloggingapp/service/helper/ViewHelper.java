package com.example.bloggingapp.service.helper;

import com.example.bloggingapp.model.Comment;
import com.example.bloggingapp.model.Post;
import com.example.bloggingapp.model.User;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;

@Component
public class ViewHelper {

    public boolean isUserViewable(User user) {
        return !user.getPrivate() && !user.getDeleted() && user.getEnabled();
    }

    public boolean isUserViewable(User user, User authUser) {
        if (authUser.equals(user)) {
            return true;
        }
        return !user.getPrivate() && !user.getDeleted() && user.getEnabled() && !user.getBlockedUsers().contains(authUser) && !authUser.getBlockedUsers().contains(user);
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

    public void filterComments(Set<Comment> comments) {
        for (Iterator<Comment> i = comments.iterator(); i.hasNext(); ) {
            Comment comment = i.next();
            if (!isCommentViewable(comment)) {
                i.remove();
            } else {
                filterComments(comment.getComments());
            }
        }
    }

    public void filterComments(Set<Comment> comments, User authUser) {
        for (Iterator<Comment> i = comments.iterator(); i.hasNext(); ) {
            Comment comment = i.next();
            if (!isCommentViewable(comment, authUser)) {
                i.remove();
            } else {
                filterComments(comment.getComments(), authUser);
            }
        }
    }

    public void filterPosts(Set<Post> posts) {
        posts.removeIf(post -> !isPostViewable(post));
    }

    public void filterPosts(Set<Post> posts, User authUser) {
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

    public void filterUsers(Set<User> users) {
        users.removeIf(user -> user.getPrivate() || user.getDeleted() || !user.getEnabled());
    }

    public void filterUsers(Set<User> users, User authUser) {
        users.removeIf(user -> !user.equals(authUser) && (user.getPrivate() || user.getDeleted() || !user.getEnabled() || user.getBlockedUsers().contains(authUser) || authUser.getBlockedUsers().contains(user)));
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
