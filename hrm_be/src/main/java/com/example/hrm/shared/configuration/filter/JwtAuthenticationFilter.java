package com.example.hrm.shared.configuration.filter;

import com.example.hrm.modules.auth.service.JwtService;
import com.example.hrm.shared.exception.AppException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("PATH = {}", request.getRequestURI());
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            var claims = jwtService.verifyAndParse(token);
            jwtService.assertAccessToken(claims);

            String username = claims.getSubject();
            String scope = claims.getStringClaim("scope");

            List<GrantedAuthority> authorities = scope == null
                    ? List.of()
                    : Stream.of(scope.split(" "))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
            log.info("Authorities = {}", authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (AppException | ParseException ex) {
            log.warn("Invalid token: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            throw new InsufficientAuthenticationException("Token invalid or expired");
        }
        filterChain.doFilter(request, response);

    }
}
