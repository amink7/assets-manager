package com.example.assets.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final ApiKeyAuthFilter apiKeyAuthFilter;
    private final String swaggerUiPath;
    private final String apiDocsPath;

    public SecurityConfig(ApiKeyAuthFilter apiKeyAuthFilter,
                          @Value("${springdoc.swagger-ui.path:/swagger-ui}") String swaggerUiPath,
                          @Value("${springdoc.api-docs.path:/v3/api-docs}") String apiDocsPath) {
        this.apiKeyAuthFilter = apiKeyAuthFilter;
        this.swaggerUiPath = swaggerUiPath;
        this.apiDocsPath = apiDocsPath;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())                    // CSRF disabled for APIs
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                swaggerUiPath,
                                swaggerUiPath + "/**",
                                swaggerUiPath + ".html",
                                apiDocsPath,
                                apiDocsPath + "/**"
                        ).permitAll()
                        // If my future boss wants GET to be public, uncomment this:
                        // .requestMatchers(HttpMethod.GET, "/api/mgmt/1/assets/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
