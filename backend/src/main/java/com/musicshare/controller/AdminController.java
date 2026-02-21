package com.musicshare.controller;

import com.musicshare.dto.request.AssignRoleRequest;
import com.musicshare.dto.request.UpdateUserStatusRequest;
import com.musicshare.dto.response.AdminDashboardResponse;
import com.musicshare.dto.response.AdminUserResponse;
import com.musicshare.dto.response.ApiResponse;
import com.musicshare.dto.response.MusicResponse;
import com.musicshare.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Controller
 *
 * <p>Handles all admin-related operations including dashboard statistics,
 * user management, and music approval workflow.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    /**
     * Get dashboard statistics
     *
     * @return Dashboard statistics
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics",
            description = "Get platform statistics for admin dashboard",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboardStatistics() {
        log.info("Admin dashboard statistics requested");
        AdminDashboardResponse response = adminService.getDashboardStatistics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all users with search and filter
     *
     * @param keyword Search keyword for username or email
     * @param enabled Filter by enabled status
     * @param page    Page number
     * @param size    Page size
     * @return Page of users
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users",
            description = "Get all users with optional search and filter",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllUsers(
            @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Filter by enabled status") @RequestParam(required = false) Boolean enabled,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        log.info("Get users request - keyword: {}, enabled: {}, page: {}, size: {}",
                keyword, enabled, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AdminUserResponse> users = adminService.getAllUsers(keyword, enabled, pageable);

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * Get user details by ID
     *
     * @param id User ID
     * @return User details
     */
    @GetMapping("/users/{id}")
    @Operation(summary = "Get user details",
            description = "Get detailed information about a specific user",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserDetails(
            @Parameter(description = "User ID") @PathVariable Long id) {

        log.info("Get user details request for userId: {}", id);
        AdminUserResponse user = adminService.getUserDetails(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Update user status (enable/disable)
     *
     * @param id      User ID
     * @param request Update status request
     * @return Success message
     */
    @PutMapping("/users/{id}/status")
    @Operation(summary = "Update user status",
            description = "Enable or disable a user account",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        log.info("Update user status request for userId: {}", id);
        adminService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", null));
    }

    /**
     * Manage user role (add or remove)
     *
     * @param request Assign role request
     * @return Success message
     */
    @PostMapping("/users/roles")
    @Operation(summary = "Manage user role",
            description = "Add or remove a role from a user",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Void>> manageUserRole(
            @Valid @RequestBody AssignRoleRequest request) {

        log.info("Manage user role request - userId: {}, role: {}, action: {}",
                request.getUserId(), request.getRoleName(), request.getAction());

        adminService.manageUserRole(request);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", null));
    }

    /**
     * Delete user by ID
     *
     * @param id User ID
     * @return Success message
     */
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user",
            description = "Permanently delete a user account (cannot delete admin users)",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        log.info("Delete user request for userId: {}", id);
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    /**
     * Get pending music for approval
     *
     * @param page Page number
     * @param size Page size
     * @return Page of pending music
     */
    @GetMapping("/music/pending")
    @Operation(summary = "Get pending music",
            description = "Get all music pending approval",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> getPendingMusic(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        log.info("Get pending music request - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<MusicResponse> pendingMusic = adminService.getPendingMusic(pageable);

        return ResponseEntity.ok(ApiResponse.success(pendingMusic));
    }

    /**
     * Get all music by status
     *
     * @param status Music status (PENDING, APPROVED, REJECTED)
     * @param page   Page number
     * @param size   Page size
     * @return Page of music
     */
    @GetMapping("/music")
    @Operation(summary = "Get music by status",
            description = "Get all music filtered by status",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Page<MusicResponse>>> getAllMusicByStatus(
            @Parameter(description = "Music status") @RequestParam String status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        log.info("Get music by status request - status: {}, page: {}, size: {}", status, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MusicResponse> music = adminService.getAllMusicByStatus(status, pageable);

        return ResponseEntity.ok(ApiResponse.success(music));
    }

    /**
     * Approve music by ID
     *
     * @param id Music ID
     * @return Success message
     */
    @PutMapping("/music/{id}/approve")
    @Operation(summary = "Approve music",
            description = "Approve a pending music track",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Void>> approveMusic(
            @Parameter(description = "Music ID") @PathVariable Long id) {

        log.info("Approve music request for musicId: {}", id);
        adminService.approveMusic(id);
        return ResponseEntity.ok(ApiResponse.success("Music approved successfully", null));
    }

    /**
     * Reject music by ID
     *
     * @param id     Music ID
     * @param reason Rejection reason (optional)
     * @return Success message
     */
    @PutMapping("/music/{id}/reject")
    @Operation(summary = "Reject music",
            description = "Reject a pending music track with optional reason",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<ApiResponse<Void>> rejectMusic(
            @Parameter(description = "Music ID") @PathVariable Long id,
            @Parameter(description = "Rejection reason") @RequestParam(required = false) String reason) {

        log.info("Reject music request for musicId: {}, reason: {}", id, reason);
        adminService.rejectMusic(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Music rejected successfully", null));
    }
}
