package com.grupo3.allslife_framework.framework.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    
    @Autowired
    TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var token = this.recoverToken(request);
            
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            
            var login = tokenService.validateToken(token);

            if (login != null) {
                logger.info("Valid Token for user: {}", login);
                
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                
                var authentication = new UsernamePasswordAuthenticationToken(
                    login,
                    null,
                    authorities
                );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.info("user authenticated: {}", login);
            } else if (token != null) {
                logger.warn("Invalid Token");
            }

            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("Security filter error: " + e.getMessage(), e);
            
            SecurityContextHolder.clearContext();
            
            if (!response.isCommitted()) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setContentType("application/json");
                response.getWriter().write("{'error':'Authentication error: " + e.getMessage() + "'}");
            } else {
                logger.warn("Response already sent");
            }
            
            filterChain.doFilter(request, response);
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}