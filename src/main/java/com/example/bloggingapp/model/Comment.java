package com.example.bloggingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 1, max = 1000)
    private String content;
    private int rating = 0;
    @ManyToOne
    private User user;
    @ManyToOne
    private Post parentPost;
    @ManyToOne
    private Comment parentComment;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "parentComment")
    private Set<Comment> comments;
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Accessors(prefix = "is")
    private Boolean isDeleted = false;
    @Accessors(prefix = "is")
    private Boolean isHidden = false;
    @Accessors(prefix = "is")
    private Boolean isDeletedByAdmin = false;
    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy;
    @ManyToMany
    @JoinTable(
            name = "comment_dislikes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> dislikedBy;

    public Comment(String content, User user, Post parentPost) {
        this.content = content;
        this.user = user;
        this.parentPost = parentPost;
    }

    public Comment(String content, User user, Post parentPost, Comment parentComment) {
        this.content = content;
        this.user = user;
        this.parentPost = parentPost;
        this.parentComment = parentComment;
    }

    @Override
    public String toString() {
        if (parentComment != null) {
            return "Comment{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    ", rating=" + rating +
                    ", user=" + user.getId() +
                    ", parentPost=" + parentPost.getId() +
                    ", parentComment=" + parentComment.getId() +
                    ", comments=" + comments +
                    ", createdAt=" + createdAt +
                    ", isDeleted=" + isDeleted +
                    ", isHidden=" + isHidden +
                    ", isDeletedByAdmin=" + isDeletedByAdmin +
                    ", likedBy=" + likedBy +
                    ", dislikedBy=" + dislikedBy +
                    '}';
        }
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", rating=" + rating +
                ", user=" + user.getId() +
                ", parentPost=" + parentPost.getId() +
                ", parentComment=null" +
                ", comments=" + comments +
                ", createdAt=" + createdAt +
                ", isDeleted=" + isDeleted +
                ", isHidden=" + isHidden +
                ", isDeletedByAdmin=" + isDeletedByAdmin +
                ", likedBy=" + likedBy.stream().map(User::getId).collect(Collectors.toSet()) +
                ", dislikedBy=" + dislikedBy.stream().map(User::getId).collect(Collectors.toSet()) +
                '}';

    }
}