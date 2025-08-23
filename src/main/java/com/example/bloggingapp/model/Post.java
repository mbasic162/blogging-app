package com.example.bloggingapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts", schema = "BloggingApp")
@NoArgsConstructor
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 5, max = 200)
    private String title;
    @NotBlank
    @Size(min = 100, max = 15000)
    private String content;
    private int rating = 0;
    @ManyToOne
    private User user;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "parentPost")
    private List<Comment> comments = new ArrayList<>();
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Accessors(prefix = "is")
    private Boolean isDeleted = false;
    @Accessors(prefix = "is")
    private Boolean isHidden = false;
    @Accessors(prefix = "is")
    private Boolean isShareableDespitePrivateUser = false;
    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            schema = "BloggingApp"
    )
    private Set<User> likedBy;
    @ManyToMany
    @JoinTable(
            name = "post_dislikes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            schema = "BloggingApp"
    )
    private Set<User> dislikedBy;

    public Post(String title, String content, User user, Boolean isHidden, Boolean isShareableDespitePrivateUser) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isHidden = isHidden;
        this.isShareableDespitePrivateUser = isShareableDespitePrivateUser;
    }
}
