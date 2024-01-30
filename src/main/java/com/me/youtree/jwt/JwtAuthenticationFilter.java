package com.me.youtree.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (token != null && jwtTokenProvider.validateTokenExpiration(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
