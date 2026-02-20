package com.musicshare.repository;

import com.musicshare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 *
 * <p>Data access layer for User entity. Provides CRUD operations and custom
 * query methods for user management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     *
     * @param username Username to search
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     *
     * @param email Email to search
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email
     *
     * @param username Username to search
     * @param email Email to search
     * @return Optional containing user if found
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Check if username exists
     *
     * @param username Username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     *
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find user by username with roles eagerly loaded
     *
     * @param username Username to search
     * @return Optional containing user with roles
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    /**
     * Find user by email with roles eagerly loaded
     *
     * @param email Email to search
     * @return Optional containing user with roles
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

}
