package com.musicshare.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Request to assign or remove a role from a user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleRequest {
    /**
     * User ID to assign/remove role
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Role name (e.g., ROLE_USER, ROLE_MUSICIAN, ROLE_ADMIN)
     */
    @NotBlank(message = "Role name is required")
    private String roleName;

    /**
     * Action to perform: ADD or REMOVE
     */
    @NotBlank(message = "Action is required")
    @Pattern(regexp = "ADD|REMOVE", message = "Action must be ADD or REMOVE")
    private String action;
}
