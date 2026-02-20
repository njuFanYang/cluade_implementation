package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity
 *
 * <p>Represents a user in the MusicShare platform. Users can be regular users,
 * musicians, or administrators with different permission levels.
 *
 * <h3>User Roles:</h3>
 * <ul>
 *   <li>USER - Regular user with basic permissions</li>
 *   <li>MUSICIAN - Can upload and manage music</li>
 *   <li>ADMIN - Full administrative access</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username (unique, 3-20 characters)
     */
    @Column(nullable = false, unique = true, length = 20)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * Email address (unique)
     */
    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Encrypted password (BCrypt)
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Nickname (display name)
     */
    @Column(length = 50)
    @Size(max = 50, message = "Nickname cannot exceed 50 characters")
    private String nickname;

    /**
     * Avatar URL
     */
    @Column(length = 500)
    private String avatar;

    /**
     * User bio/description
     */
    @Column(length = 500)
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    /**
     * Gender (M: Male, F: Female, O: Other, U: Unspecified)
     */
    @Column(length = 1)
    private String gender;

    /**
     * Phone number
     */
    @Column(length = 20)
    private String phone;

    /**
     * Location/Region
     */
    @Column(length = 100)
    private String location;

    /**
     * Account status (true: active, false: disabled)
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * Account locked status
     */
    @Column(nullable = false)
    private Boolean locked = false;

    /**
     * User roles (many-to-many relationship)
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Number of followers
     */
    @Column(nullable = false)
    private Integer followersCount = 0;

    /**
     * Number of following
     */
    @Column(nullable = false)
    private Integer followingCount = 0;

    /**
     * Number of uploaded music (for musicians)
     */
    @Column(nullable = false)
    private Integer musicCount = 0;

    /**
     * Number of playlists created
     */
    @Column(nullable = false)
    private Integer playlistCount = 0;

    /**
     * Last login time
     */
    @Column
    private LocalDateTime lastLoginTime;

    /**
     * Last login IP address
     */
    @Column(length = 50)
    private String lastLoginIp;

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Helper method to add a role
     *
     * @param role Role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Helper method to remove a role
     *
     * @param role Role to remove
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Check if user has a specific role
     *
     * @param roleName Role name to check
     * @return true if user has the role
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

}
