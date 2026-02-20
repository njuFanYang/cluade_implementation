package com.musicshare.exception;

import lombok.Getter;

/**
 * Error Code Enumeration
 *
 * <p>Defines all possible error codes used throughout the application for
 * consistent error handling and client communication.
 *
 * <h3>Code Ranges:</h3>
 * <ul>
 *   <li>1000-1999: User related errors</li>
 *   <li>2000-2999: Music related errors</li>
 *   <li>3000-3999: Playlist related errors</li>
 *   <li>4000-4999: Social interaction errors</li>
 *   <li>5000-5999: Admin/System errors</li>
 *   <li>9000-9999: Common errors</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Getter
public enum ErrorCode {

    // Common errors (9000-9999)
    SUCCESS(200, "Success"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"),
    VALIDATION_ERROR(9001, "Validation error"),
    INVALID_PARAMETER(9002, "Invalid parameter"),
    OPERATION_FAILED(9003, "Operation failed"),

    // User errors (1000-1999)
    USER_NOT_FOUND(1001, "User not found"),
    USER_ALREADY_EXISTS(1002, "User already exists"),
    INVALID_CREDENTIALS(1003, "Invalid username or password"),
    TOKEN_EXPIRED(1004, "Token has expired"),
    TOKEN_INVALID(1005, "Invalid token"),
    PASSWORD_MISMATCH(1006, "Password mismatch"),
    EMAIL_ALREADY_EXISTS(1007, "Email already exists"),
    USERNAME_ALREADY_EXISTS(1008, "Username already exists"),
    INSUFFICIENT_PERMISSIONS(1009, "Insufficient permissions"),
    ACCOUNT_DISABLED(1010, "Account has been disabled"),
    OLD_PASSWORD_INCORRECT(1011, "Old password is incorrect"),

    // Music errors (2000-2999)
    MUSIC_NOT_FOUND(2001, "Music not found"),
    MUSIC_UPLOAD_FAILED(2002, "Music upload failed"),
    INVALID_MUSIC_FORMAT(2003, "Invalid music file format"),
    MUSIC_FILE_TOO_LARGE(2004, "Music file size exceeds limit"),
    MUSIC_ALREADY_EXISTS(2005, "Music already exists"),
    MUSIC_NOT_APPROVED(2006, "Music not approved yet"),
    MUSIC_PROCESSING(2007, "Music is being processed"),

    // Playlist errors (3000-3999)
    PLAYLIST_NOT_FOUND(3001, "Playlist not found"),
    PLAYLIST_NAME_EXISTS(3002, "Playlist name already exists"),
    MUSIC_ALREADY_IN_PLAYLIST(3003, "Music already in playlist"),
    MUSIC_NOT_IN_PLAYLIST(3004, "Music not in playlist"),
    CANNOT_MODIFY_PLAYLIST(3005, "Cannot modify this playlist"),

    // Social interaction errors (4000-4999)
    COMMENT_NOT_FOUND(4001, "Comment not found"),
    ALREADY_LIKED(4002, "Already liked"),
    NOT_LIKED_YET(4003, "Not liked yet"),
    ALREADY_FOLLOWING(4004, "Already following this user"),
    NOT_FOLLOWING_YET(4005, "Not following this user"),
    CANNOT_FOLLOW_SELF(4006, "Cannot follow yourself"),
    COMMENT_TOO_LONG(4007, "Comment exceeds maximum length"),

    // Admin/System errors (5000-5999)
    AUDIT_RECORD_NOT_FOUND(5001, "Audit record not found"),
    AUDIT_ALREADY_PROCESSED(5002, "Audit record already processed"),
    REPORT_NOT_FOUND(5003, "Report not found"),
    INVALID_AUDIT_STATUS(5004, "Invalid audit status"),

    // File errors (6000-6999)
    FILE_UPLOAD_FAILED(6001, "File upload failed"),
    FILE_NOT_FOUND(6002, "File not found"),
    FILE_TOO_LARGE(6003, "File size exceeds limit"),
    INVALID_FILE_TYPE(6004, "Invalid file type"),
    FILE_READ_ERROR(6005, "File read error"),
    FILE_DELETE_FAILED(6006, "File delete failed");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
