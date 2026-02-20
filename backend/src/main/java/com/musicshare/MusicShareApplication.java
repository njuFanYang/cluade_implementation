package com.musicshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MusicShare Application Entry Point
 *
 * <p>Main class for the MusicShare music community platform backend service.
 * This application provides RESTful APIs for music sharing, social interaction,
 * and content management.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>User authentication and authorization (JWT)</li>
 *   <li>Music upload, playback, and management</li>
 *   <li>Playlist creation and sharing</li>
 *   <li>Social features (comments, likes, follows)</li>
 *   <li>Admin dashboard and content moderation</li>
 * </ul>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@SpringBootApplication
public class MusicShareApplication {

    /**
     * Application entry point
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MusicShareApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🎵 MusicShare Backend Started Successfully!");
        System.out.println("📚 API Documentation: http://localhost:8081/swagger-ui.html");
        System.out.println("🔧 Server Port: 8081");
        System.out.println("========================================\n");
    }

}
