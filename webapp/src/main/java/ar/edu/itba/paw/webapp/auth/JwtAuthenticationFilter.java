package ar.edu.itba.paw.webapp.auth;

import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String jws = authHeader.substring(BEARER_PREFIX.length()).trim();
            if(jws.isEmpty()) {
                rejectInvalidToken(request, response);
                return;
            }
            UserDetails userDetails = null;
            try {
                userDetails = jwtTokenService.validateJwsToken(jws);
            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
                rejectInvalidToken(request, response);
                return;
            }
            Authentication authentication = null;
            if (userDetails != null) {
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContext securityContext = SecurityContextHolder.getContext();
                if(securityContext.getAuthentication() == null) {
                    securityContext.setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void rejectInvalidToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        apiAuthenticationEntryPoint.commence(
                request,
                response,
                new BadCredentialsException("Invalid bearer token")
        );
    }
}
