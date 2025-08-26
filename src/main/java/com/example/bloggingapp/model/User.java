package com.example.bloggingapp.model;

import com.example.bloggingapp.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "USERS", schema = "BloggingApp")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 2, max = 50)
    private String name;
    @Size(min = 3, max = 20)
    private String username;
    @Email
    @Size(min = 3, max = 50)
    private String email;
    private String password;
    private final LocalDate createdOn = LocalDate.now();
    @Accessors(prefix = "is")
    private Boolean isPrivate = false;
    @Accessors(prefix = "is")
    private Boolean isEnabled = true;
    @Accessors(prefix = "is")
    private Boolean isDeleted = false;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private List<Role> roles = List.of(Role.USER);

    @ManyToMany
    @JoinTable(
            name = "followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id"),
            schema = "BloggingApp"
    )
    private Set<User> followers;

    @ManyToMany(mappedBy = "followers")
    private Set<User> following;

    @ManyToMany
    @JoinTable(
            name = "blocked_users",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            schema = "BloggingApp"
    )
    private Set<User> blockedUsers;

    @OneToMany(orphanRemoval = true, mappedBy = "user")
    private Set<Post> posts;

    @OneToMany(orphanRemoval = true, mappedBy = "user")
    private Set<Comment> comments;

    @ManyToMany(mappedBy = "likedBy")
    private Set<Post> likedPosts;

    @ManyToMany(mappedBy = "dislikedBy")
    private Set<Post> dislikedPosts;

    @ManyToMany(mappedBy = "likedBy")
    private Set<Comment> likedComments;

    @ManyToMany(mappedBy = "dislikedBy")
    private Set<Comment> dislikedComments;

    public User(String name, String username, String email, String password, boolean isPrivate) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isPrivate = isPrivate;
    }

}
