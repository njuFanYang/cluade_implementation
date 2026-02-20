package com.musicshare.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Request to update user status (enable/disable)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {
    /**
     * Whether to enable or disable the user account
     */
    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    /**
     * Whether to lock or unlock the user account (optional)
     */
    private Boolean locked;
}
