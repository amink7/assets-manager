package com.example.assets.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String expectedKey;
    private final String swaggerUiPath;
    private final String apiDocsPath;

    public ApiKeyAuthFilter(@Value("${security.api.key}") String expectedKey,
                            @Value("${springdoc.swagger-ui.path:/swagger-ui}") String swaggerUiPath,
                            @Value("${springdoc.api-docs.path:/v3/api-docs}") String apiDocsPath) {
        this.expectedKey = expectedKey;
        this.swaggerUiPath = swaggerUiPath;
        this.apiDocsPath = apiDocsPath;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith(swaggerUiPath) || path.startsWith(apiDocsPath)) {
            chain.doFilter(request, response);
            return;
        }

        String key = request.getHeader("X-API-KEY");
        if (key == null || !key.equals(expectedKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or missing API Key");
            return;
        }

        // mark as authenticated (avoids subsequent 403s)
        var auth = new UsernamePasswordAuthenticationToken(
                "api-key-user", null, List.of(new SimpleGrantedAuthority("ROLE_API"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
