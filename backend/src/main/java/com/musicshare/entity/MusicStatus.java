package com.musicshare.entity;

/**
 * Music Status Enumeration
 *
 * <p>Represents the approval status of uploaded music.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
public enum MusicStatus {
    /**
     * Music is pending approval
     */
    PENDING,

    /**
     * Music has been approved and is publicly available
     */
    APPROVED,

    /**
     * Music has been rejected
     */
    REJECTED
}
