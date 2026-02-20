package com.musicshare.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Security Utility Class
 *
 * <p>Provides utility methods for accessing security context information
 * and checking user permissions. This class simplifies common security-related
 * operations throughout the application.
 *
 * <h3>Main Features:</h3>
 * <ul>
 *   <li>Get currently authenticated user information</li>
 *   <li>Check if a username matches the current user</li>
 *   <li>Check if current user has specific roles</li>
 *   <li>Get authentication object</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * // Get current username
 * String username = SecurityUtil.getCurrentUsername();
 *
 * // Check if user can modify resource
 * if (SecurityUtil.isCurrentUser(resourceOwner) || SecurityUtil.hasRole("ROLE_ADMIN")) {
 *     // Allow modification
 * }
 *
 * // Check for specific role
 * if (SecurityUtil.hasRole("ROLE_MUSICIAN")) {
 *     // Allow music upload
 * }
 * </pre>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
public class SecurityUtil {

    /**
     * Private constructor to prevent instantiation
     */
    private SecurityUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Get current authenticated username
     *
     * <p>Retrieves the username of the currently authenticated user from the
     * Spring Security context. Returns null if no user is authenticated.
     *
     * @return Username of authenticated user, or null if not authenticated
     */
    public static String getCurrentUsername() {
        return getAuthentication()
                .map(auth -> {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof UserDetails) {
                        return ((UserDetails) principal).getUsername();
                    } else if (principal instanceof String) {
                        return (String) principal;
                    }
                    return null;
                })
                .orElse(null);
    }

    /**
     * Check if the given username matches the current authenticated user
     *
     * <p>Compares the provided username with the currently authenticated user's
     * username. This is useful for authorization checks to ensure users can only
     * modify their own resources.
     *
     * @param username Username to check against current user
     * @return true if the username matches the current authenticated user, false otherwise
     */
    public static boolean isCurrentUser(String username) {
        if (username == null) {
            return false;
        }

        String currentUsername = getCurrentUsername();
        if (currentUsername == null) {
            return false;
        }

        return username.equals(currentUsername);
    }

    /**
     * Check if current user has a specific role
     *
     * <p>Checks whether the currently authenticated user has the specified role.
     * Role names should follow the Spring Security convention (e.g., "ROLE_USER",
     * "ROLE_ADMIN", "ROLE_MUSICIAN").
     *
     * <h3>Usage Examples:</h3>
     * <pre>
     * SecurityUtil.hasRole("ROLE_ADMIN")
     * SecurityUtil.hasRole("ROLE_MUSICIAN")
     * SecurityUtil.hasRole("ROLE_USER")
     * </pre>
     *
     * @param role Role name to check (e.g., "ROLE_ADMIN")
     * @return true if current user has the specified role, false otherwise
     */
    public static boolean hasRole(String role) {
        if (role == null) {
            return false;
        }

        return getAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(authority -> authority.equals(role)))
                .orElse(false);
    }

    /**
     * Check if current user has any of the specified roles
     *
     * <p>Checks whether the currently authenticated user has at least one of
     * the specified roles. This is useful when multiple roles should have access
     * to a resource.
     *
     * <h3>Usage Example:</h3>
     * <pre>
     * if (SecurityUtil.hasAnyRole("ROLE_ADMIN", "ROLE_MUSICIAN")) {
     *     // Allow music upload
     * }
     * </pre>
     *
     * @param roles Variable number of role names to check
     * @return true if current user has at least one of the specified roles, false otherwise
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user has all of the specified roles
     *
     * <p>Checks whether the currently authenticated user has all of the
     * specified roles. This is useful for resources that require multiple
     * role permissions.
     *
     * @param roles Variable number of role names to check
     * @return true if current user has all specified roles, false otherwise
     */
    public static boolean hasAllRoles(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get current authentication object
     *
     * <p>Retrieves the Spring Security Authentication object for the currently
     * authenticated user. Returns an empty Optional if no user is authenticated.
     *
     * @return Optional containing Authentication object, or empty if not authenticated
     */
    public static Optional<Authentication> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.of(authentication);
    }

    /**
     * Check if user is authenticated
     *
     * <p>Determines whether there is a currently authenticated user in the
     * security context.
     *
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return getAuthentication().isPresent();
    }

    /**
     * Check if current user is admin
     *
     * <p>Convenience method to check if the current user has admin privileges.
     * Equivalent to calling hasRole("ROLE_ADMIN").
     *
     * @return true if current user is an admin, false otherwise
     */
    public static boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * Check if current user is a musician
     *
     * <p>Convenience method to check if the current user has musician privileges.
     * Equivalent to calling hasRole("ROLE_MUSICIAN").
     *
     * @return true if current user is a musician, false otherwise
     */
    public static boolean isMusician() {
        return hasRole("ROLE_MUSICIAN");
    }

    /**
     * Check if current user can modify resource
     *
     * <p>Checks whether the current user has permission to modify a resource.
     * Permission is granted if the user is either the owner of the resource
     * or an administrator.
     *
     * @param resourceOwner Username of the resource owner
     * @return true if current user can modify the resource, false otherwise
     */
    public static boolean canModifyResource(String resourceOwner) {
        return isCurrentUser(resourceOwner) || isAdmin();
    }

}
