package com.musicshare.exception;

import com.musicshare.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 *
 * <p>Centralized exception handling for all controllers. Catches exceptions
 * and converts them to standardized API responses.
 *
 * <h3>Handled Exceptions:</h3>
 * <ul>
 *   <li>BusinessException - Custom business logic errors</li>
 *   <li>ValidationException - Request validation errors</li>
 *   <li>AuthenticationException - Authentication failures</li>
 *   <li>AccessDeniedException - Authorization failures</li>
 *   <li>General exceptions - Unexpected errors</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle BusinessException
     *
     * @param e BusinessException
     * @return Error response
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.warn("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error(e.getCode(), e.getMessage(), e.getData()));
    }

    /**
     * Handle validation exceptions
     *
     * @param e MethodArgumentNotValidException
     * @return Error response with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        "Validation failed",
                        errors
                ));
    }

    /**
     * Handle bind exceptions
     *
     * @param e BindException
     * @return Error response with field errors
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBindException(BindException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Bind exception: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        "Invalid request parameters",
                        errors
                ));
    }

    /**
     * Handle method argument type mismatch
     *
     * @param e MethodArgumentTypeMismatchException
     * @return Error response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format("Parameter '%s' should be of type %s",
                e.getName(), e.getRequiredType().getSimpleName());
        log.warn("Type mismatch: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_PARAMETER.getCode(), message));
    }

    /**
     * Handle authentication exceptions
     *
     * @param e AuthenticationException
     * @return Error response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());

        if (e instanceof BadCredentialsException) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(
                            ErrorCode.INVALID_CREDENTIALS.getCode(),
                            ErrorCode.INVALID_CREDENTIALS.getMessage()
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.UNAUTHORIZED.getCode(), e.getMessage()));
    }

    /**
     * Handle access denied exceptions
     *
     * @param e AccessDeniedException
     * @return Error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        ErrorCode.FORBIDDEN.getCode(),
                        ErrorCode.FORBIDDEN.getMessage()
                ));
    }

    /**
     * Handle 404 - No handler found
     *
     * @param e NoHandlerFoundException
     * @return Error response
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NoHandlerFoundException e) {
        log.warn("Resource not found: {} {}", e.getHttpMethod(), e.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        ErrorCode.NOT_FOUND.getCode(),
                        "Resource not found: " + e.getRequestURL()
                ));
    }

    /**
     * Handle all other exceptions
     *
     * @param e Exception
     * @return Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        "An unexpected error occurred. Please try again later."
                ));
    }

}
