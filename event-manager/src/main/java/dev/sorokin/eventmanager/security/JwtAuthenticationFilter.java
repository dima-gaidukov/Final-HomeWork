package dev.sorokin.eventmanager.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final  JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwtToken = header.substring(7);

        Long loginIdFromToken;
        String role;

        try {
            loginIdFromToken = jwtTokenProvider.getUserIdFromToken(jwtToken);

            role = jwtTokenProvider.getRoleFromToken(jwtToken);

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(loginIdFromToken,null, authorities);

            SecurityContextHolder.getContext().setAuthentication(token);

        }catch (Exception e){
            log.info("Invalid JWT Token: {}", e.getMessage());
            SecurityContextHolder.clearContext();

        }
        filterChain.doFilter(request, response);

    }
}
