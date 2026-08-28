package com.healthcare.platform.security.auth;
import com.healthcare.platform.model.auth.*;
import com.healthcare.platform.repository.auth.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Stateless JWT authentication for API clients (Postman, mobile apps, etc.).
 *
 * This runs alongside - not instead of - the team's existing session/cookie login.
 * If a request has no "Authorization: Bearer ..." header, this filter does nothing
 * and the request falls through to the existing form-login/session handling untouched.
 * It intentionally looks the user up via AuthUserJdbcRepository (no ORM) rather than
 * the JPA-backed UserDetailsService, so this whole JWT path stays ORM-free end to end.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthUserJdbcRepository authUsers;

    public JwtAuthFilter(JwtService jwtService, AuthUserJdbcRepository authUsers) {
        this.jwtService = jwtService;
        this.authUsers = authUsers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractBearerToken(request);

        if (token != null && jwtService.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtService.extractEmail(token);
            Optional<AuthUser> user = email != null ? authUsers.findByEmail(email) : Optional.empty();

            if (user.isPresent() && user.get().isActive()) {
                String authority = "ROLE_" + user.get().getRole().name();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.get().getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
