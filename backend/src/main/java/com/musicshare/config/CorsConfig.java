package com.musicshare.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS Configuration
 *
 * <p>Configures Cross-Origin Resource Sharing (CORS) to allow frontend
 * applications from different origins to access the API.
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials}")
    private Boolean allowCredentials;

    @Value("${cors.max-age}")
    private Long maxAge;

    /**
     * Configure CORS filter
     *
     * @return CorsFilter instance
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Set allowed origins
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));

        // Set allowed methods
        config.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));

        // Set allowed headers
        if ("*".equals(allowedHeaders)) {
            config.addAllowedHeader("*");
        } else {
            config.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }

        // Allow credentials
        config.setAllowCredentials(allowCredentials);

        // Set max age
        config.setMaxAge(maxAge);

        // Expose headers
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}
