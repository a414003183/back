package com.telecom.scm.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.telecom.scm.security.model.AuthenticatedUser;
import com.telecom.scm.security.service.CurrentUserMenuService;
import com.telecom.scm.security.service.IdentitySessionService;
import com.telecom.scm.security.service.TokenService;
import com.telecom.scm.security.service.UserAuthService;

import io.jsonwebtoken.Claims;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final TokenService tokenService;
    private final UserAuthService userAuthService;
    private final IdentitySessionService identitySessionService;
    private final CurrentUserMenuService currentUserMenuService;

    public JwtAuthenticationFilter(
            TokenService tokenService,
            UserAuthService userAuthService,
            IdentitySessionService identitySessionService,
            CurrentUserMenuService currentUserMenuService) {
        this.tokenService = tokenService;
        this.userAuthService = userAuthService;
        this.identitySessionService = identitySessionService;
        this.currentUserMenuService = currentUserMenuService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                log.info("=== JWT Filter: Processing token ===");
                if (tokenService.isTokenValid(token)) {
                    Claims claims = tokenService.parseClaims(token);
                    String username = claims.getSubject();
                    String identityType = claims.get("identityType", String.class);
                    log.info(
                            "=== JWT Filter: Looking up user: {} with identityType: {} ===",
                            username,
                            identityType);
                    userAuthService
                            .findByUsernameAndIdentity(username, identityType)
                            .ifPresent(
                                    user -> {
                                        log.info(
                                                "=== JWT Filter: Found user: {}, role: {}, identityType: {}",
                                                user.username(),
                                                user.role(),
                                                user.identityType());
                                        identitySessionService.syncActiveIdentity(user);

                                        // Use permissions from userAuthService (includes BUTTON
                                        // type menus)
                                        List<String> permissions = user.permissions();

                                        // Create a new AuthenticatedUser with the extracted
                                        // permissions
                                        AuthenticatedUser userWithPermissions =
                                                new AuthenticatedUser(
                                                        user.userId(),
                                                        user.username(),
                                                        user.passwordHash(),
                                                        user.role(),
                                                        user.identityType(),
                                                        user.memberId(),
                                                        user.displayName(),
                                                        user.route(),
                                                        permissions);

                                        List<SimpleGrantedAuthority> authorities =
                                                new ArrayList<>();
                                        authorities.add(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_" + userWithPermissions.role()));
                                        permissions.forEach(
                                                permission ->
                                                        authorities.add(
                                                                new SimpleGrantedAuthority(
                                                                        "PERM_" + permission)));
                                        UsernamePasswordAuthenticationToken authentication =
                                                new UsernamePasswordAuthenticationToken(
                                                        userWithPermissions, null, authorities);
                                        SecurityContextHolder.getContext()
                                                .setAuthentication(authentication);
                                        log.info(
                                                "=== JWT Filter: Authentication set successfully with {} permissions ===",
                                                permissions.size());
                                    });
                } else {
                    log.info("=== JWT Filter: Token is invalid or expired ===");
                }
            } catch (Exception exception) {
                log.warn("=== JWT Filter: Failed to parse token: {} ===", exception.getMessage());
            }
        } else {
            log.info("=== JWT Filter: No Authorization header or not Bearer token ===");
        }

        filterChain.doFilter(request, response);
    }
}
