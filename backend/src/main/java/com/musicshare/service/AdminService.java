package com.musicshare.service;

import com.musicshare.dto.request.AssignRoleRequest;
import com.musicshare.dto.request.UpdateUserStatusRequest;
import com.musicshare.dto.response.AdminDashboardResponse;
import com.musicshare.dto.response.AdminUserResponse;
import com.musicshare.dto.response.MusicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Admin Service Interface
 *
 * <p>Service layer for admin operations including dashboard statistics,
 * user management, and music approval workflow.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
public interface AdminService {

    /**
     * Get dashboard statistics
     *
     * @return Dashboard statistics
     */
    AdminDashboardResponse getDashboardStatistics();

    /**
     * Get all users with search and filter
     *
     * @param keyword  Search keyword for username or email
     * @param enabled  Filter by enabled status (null for all)
     * @param pageable Pagination parameters
     * @return Page of admin user responses
     */
    Page<AdminUserResponse> getAllUsers(String keyword, Boolean enabled, Pageable pageable);

    /**
     * Get user details by ID
     *
     * @param userId User ID
     * @return Admin user response
     */
    AdminUserResponse getUserDetails(Long userId);

    /**
     * Update user status (enable/disable)
     *
     * @param userId  User ID
     * @param request Update status request
     */
    void updateUserStatus(Long userId, UpdateUserStatusRequest request);

    /**
     * Assign or remove role from user
     *
     * @param request Assign role request
     */
    void manageUserRole(AssignRoleRequest request);

    /**
     * Delete user by ID
     *
     * @param userId User ID
     */
    void deleteUser(Long userId);

    /**
     * Get pending music for approval
     *
     * @param pageable Pagination parameters
     * @return Page of music responses
     */
    Page<MusicResponse> getPendingMusic(Pageable pageable);

    /**
     * Approve music by ID
     *
     * @param musicId Music ID
     */
    void approveMusic(Long musicId);

    /**
     * Reject music by ID with optional reason
     *
     * @param musicId Music ID
     * @param reason  Rejection reason (optional)
     */
    void rejectMusic(Long musicId, String reason);

    /**
     * Get all music by status
     *
     * @param status   Music status (PENDING, APPROVED, REJECTED)
     * @param pageable Pagination parameters
     * @return Page of music responses
     */
    Page<MusicResponse> getAllMusicByStatus(String status, Pageable pageable);
}
