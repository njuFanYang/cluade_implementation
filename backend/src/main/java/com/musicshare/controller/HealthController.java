package com.musicshare.controller;

import com.musicshare.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 *
 * <p>Provides health check and status endpoints for monitoring
 * the application status.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health check APIs")
public class HealthController {

    /**
     * Health check endpoint
     *
     * @return Health status
     */
    @GetMapping
    @Operation(summary = "Check application health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "MusicShare Backend");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now());

        return ApiResponse.success("Application is running", health);
    }

    /**
     * Simple ping endpoint
     *
     * @return Pong message
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping the server")
    public ApiResponse<String> ping() {
        return ApiResponse.success("pong");
    }

}
