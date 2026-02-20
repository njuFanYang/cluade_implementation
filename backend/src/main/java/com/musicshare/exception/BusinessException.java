package com.musicshare.exception;

import lombok.Getter;

/**
 * Business Exception
 *
 * <p>Custom exception for business logic errors. This exception is used to
 * represent expected error conditions in business operations.
 *
 * <h3>Usage Example:</h3>
 * <pre>
 * if (user == null) {
 *     throw new BusinessException(ErrorCode.USER_NOT_FOUND);
 * }
 * </pre>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code
     */
    private final Integer code;

    /**
     * Error message
     */
    private final String message;

    /**
     * Additional error data
     */
    private final Object data;

    /**
     * Constructor with ErrorCode
     *
     * @param errorCode Error code enum
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }

    /**
     * Constructor with ErrorCode and custom message
     *
     * @param errorCode Error code enum
     * @param customMessage Custom error message
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.data = null;
    }

    /**
     * Constructor with ErrorCode and additional data
     *
     * @param errorCode Error code enum
     * @param data Additional error data
     */
    public BusinessException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = data;
    }

    /**
     * Constructor with code and message
     *
     * @param code Error code
     * @param message Error message
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = null;
    }

    /**
     * Constructor with all parameters
     *
     * @param code Error code
     * @param message Error message
     * @param data Additional error data
     */
    public BusinessException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }

}
