package com.musicshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Role Entity
 *
 * <p>Represents a user role with associated permissions. Roles define what
 * actions users can perform in the system.
 *
 * <h3>Standard Roles:</h3>
 * <ul>
 *   <li>ROLE_USER - Basic user permissions</li>
 *   <li>ROLE_MUSICIAN - Music upload and management</li>
 *   <li>ROLE_ADMIN - Full administrative access</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name (e.g., ROLE_USER, ROLE_MUSICIAN, ROLE_ADMIN)
     */
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Role name is required")
    private String name;

    /**
     * Role display name
     */
    @Column(length = 50)
    private String displayName;

    /**
     * Role description
     */
    @Column(length = 200)
    private String description;

    /**
     * Users with this role (many-to-many relationship)
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    /**
     * Creation timestamp
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor for creating a role with name only
     *
     * @param name Role name
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Constructor for creating a role with name and display name
     *
     * @param name Role name
     * @param displayName Display name
     */
    public Role(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

}
