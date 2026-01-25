package ar.edu.itba.paw.webapp.auth;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static int JWS_STRING_POS = 1;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader.startsWith("Bearer ")) {
            String jws = authHeader.split(" ")[JWS_STRING_POS];
            UserDetails userDetails = jwtTokenService.validateJwsToken(jws);
            Authentication token = new UsernamePasswordAuthenticationToken(userDetails, null);
        }
        filterChain.doFilter(request, response);
    }
}
