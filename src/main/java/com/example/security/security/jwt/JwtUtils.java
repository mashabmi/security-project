package com.example.security.security.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.example.security.model.User;
import com.example.security.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.application.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.application.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.application.jwtCookieName}")
    private String jwtCookie;

    @Value("${spring.application.jwtRefreshCookieName}")
    private String jwtRefreshCookie;

    
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
        return cookie;
    }
    
    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateTokenFromUsername(user.getUsername());   
        return generateCookie(jwtCookie, jwt, "/api");
    }
    
    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/api/auth/refreshtoken");
    }
    
    public String getJwtFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }
    
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
            .parseClaimsJws(token).getBody().getSubject();
    }
      
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }

        return false;
    }

    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
        return cookie;
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtRefreshCookie, null).path("/api/auth/refreshtoken").build();
        return cookie;
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)
            .path(path)
            .maxAge(15 * 60) // 15 minuta
            .httpOnly(true)
            .sameSite("None") // Allow cross-origin cookies
            .secure(true) // Required for SameSite=None
            .build();
    }
      
    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return (cookie != null) ? cookie.getValue() : null;
    }
}
