package com.musicshare.repository;

import com.musicshare.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Role Repository
 *
 * <p>Data access layer for Role entity. Provides CRUD operations and custom
 * query methods for role management.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     *
     * @param name Role name to search
     * @return Optional containing role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Check if role exists by name
     *
     * @param name Role name to check
     * @return true if role exists
     */
    boolean existsByName(String name);

}
