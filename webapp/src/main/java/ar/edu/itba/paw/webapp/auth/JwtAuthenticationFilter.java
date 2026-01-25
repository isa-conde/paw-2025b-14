package ar.edu.itba.paw.webapp.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String jws = authHeader.substring(BEARER_PREFIX.length()).trim();
            UserDetails userDetails = null;
            try {
                userDetails = jwtTokenService.validateJwsToken(jws);
            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if(securityContext.getAuthentication() == null) {
                securityContext.setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
