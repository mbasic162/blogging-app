package com.example.bloggingapp.repository;

import com.example.bloggingapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsById(@NonNull Long id);

    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO followers (user_id, parent_id) VALUES (:user_id, :parent_id)", nativeQuery = true)
    void follow(@Param("user_id") Long userId, @Param("parent_id") Long parentId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM followers WHERE user_id = :user_id AND parent_id = :parent_id", nativeQuery = true)
    void unfollow(@Param("user_id") Long userId, @Param("parent_id") Long parentId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO blocked_users (parent_id, user_id) VALUES (:parent_id, :user_id)", nativeQuery = true)
    void block(@Param("user_id") Long userId, @Param("parent_id") Long parentId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM blocked_users WHERE parent_id = :parent_id AND user_id = :user_id", nativeQuery = true)
    void unblock(@Param("user_id") Long userId, @Param("parent_id") Long parentId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.isDeleted = true WHERE u.id = :user_id")
    void tempDelete(@Param("user_id") Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.isDeleted = false WHERE u.id = :user_id")
    void undelete(@Param("user_id") Long userId);
}