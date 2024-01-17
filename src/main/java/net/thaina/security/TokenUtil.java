package net.thaina.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import net.thaina.domain.AuthToken;
import net.thaina.domain.Users;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Collections;

public class TokenUtil {

    private static final String EMISSOR = "auth";
    private static final String TOKEN_HEADER = "Bearer ";
    private static final String TOKEN_KEY = "04b6e1a104ba0ed5e7985abde3e13140";
    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUT = 60 * ONE_SECOND;

    public static AuthToken encodeToken(Users user) {
        Key secret = Keys.hmacShaKeyFor(TOKEN_KEY.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject(user.getLogin())
                .issuer(EMISSOR)
                .expiration(new Date(System.currentTimeMillis() + ONE_MINUT))
                .signWith(secret, SignatureAlgorithm.HS256).compact();

        AuthToken finalToken = new AuthToken(TOKEN_HEADER + token);
        return finalToken;
    }

    public static Authentication decodeToken(HttpServletRequest req) {

        String token = req.getHeader("Authorization");
        token = token.replace(TOKEN_HEADER, "");

        boolean isTokenValid = validTokenJwt(token);

        if (isTokenValid) {
            return new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
        }
        return null;
    }


    public static boolean validTokenJwt(String token) {
        Jws<Claims> jwtClaims = (Jws<Claims>) Jwts.parser().setSigningKey(TOKEN_KEY.getBytes()).build().parse(token);

        String user = jwtClaims.getBody().getSubject();
        String issuer = jwtClaims.getBody().getIssuer();
        Date expiration = jwtClaims.getBody().getExpiration();

        if (user.length() > 0 && issuer.equals(EMISSOR) && expiration.after(new Date(System.currentTimeMillis()))) {
            return true;
        }
        return false;
    }
}
