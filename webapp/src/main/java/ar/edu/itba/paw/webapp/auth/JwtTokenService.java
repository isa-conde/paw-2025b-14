package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenService {

    @Autowired
    private UserDetailsService uds;

    private static final int EXPIRY_TIME = 86400000;

    private final SecretKey jwtKey;

    public JwtTokenService(@Value("classpath:jwt.key") Resource jwtKeyResource) throws IOException {
        String base64Key = new String(jwtKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        this.jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Key));
    }

    public String createJwsToken(User user) {
        long currentTime = System.currentTimeMillis();

        return "Bearer " + Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + EXPIRY_TIME))
                .signWith(jwtKey)
                .compact();
    }

    public UserDetails validateJwsToken(String jws) throws JwtException {
        JwtParser parser = Jwts.parser().verifyWith(jwtKey).build();
        Jws<Claims> readJwsToken = parser.parseSignedClaims(jws);
        Claims claims = readJwsToken.getPayload();
        return (UserDetails) uds.loadUserByUsername(claims.getSubject());
    }

}
