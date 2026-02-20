package com.musicshare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Unified API Response Wrapper
 *
 * <p>Standard response format for all API endpoints to ensure consistency
 * across the application.
 *
 * <h3>Response Structure:</h3>
 * <pre>
 * {
 *   "code": 200,
 *   "message": "Success",
 *   "data": { ... },
 *   "timestamp": 1707987654321
 * }
 * </pre>
 *
 * @param <T> Type of response data
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Response code (200 = success, others = error)
     */
    private Integer code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data payload
     */
    private T data;

    /**
     * Response timestamp
     */
    private Long timestamp;

    /**
     * Create success response with data
     *
     * @param data Response data
     * @param <T> Data type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, System.currentTimeMillis());
    }

    /**
     * Create success response with custom message
     *
     * @param message Success message
     * @param data Response data
     * @param <T> Data type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, System.currentTimeMillis());
    }

    /**
     * Create success response without data
     *
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "Success", null, System.currentTimeMillis());
    }

    /**
     * Create error response
     *
     * @param code Error code
     * @param message Error message
     * @param <T> Data type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * Create error response with data
     *
     * @param code Error code
     * @param message Error message
     * @param data Error data
     * @param <T> Data type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data, System.currentTimeMillis());
    }

}
