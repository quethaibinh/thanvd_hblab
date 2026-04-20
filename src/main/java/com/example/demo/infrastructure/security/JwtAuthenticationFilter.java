package com.example.demo.infrastructure.security;

import com.example.demo.shared.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public JwtAuthenticationFilter(
            JwtTokenService jwtTokenService,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.jwtTokenService = jwtTokenService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7).trim();
            if (!token.isBlank()) {
                try {
                    AuthenticatedUserPrincipal principal = jwtTokenService.parseAndValidate(token);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            token,
                            principal.authorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (UnauthorizedException exception) {
                    SecurityContextHolder.clearContext();
                    restAuthenticationEntryPoint.commence(request, response, new org.springframework.security.authentication.BadCredentialsException(exception.getMessage(), exception));
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
