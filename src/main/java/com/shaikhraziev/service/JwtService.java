package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.entity.Role;
import com.shaikhraziev.util.PropertiesUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JwtService {

    private final String SECRET_KEY = PropertiesUtil.get("SECRET_KEY");

    /**
     * Генерация токена
     *
     * @param user пользователь
     * @return токен
     */
    public String generateToken(UserReadDto user) {
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();

        String alg = "HS256";
        String typ = "JWT";

        Long userId = user.getId();
        Role role = user.getRole();

        header.put("alg", alg);
        header.put("typ", typ);

        payload.put("userId", userId);
        payload.put("role", role);

        return Jwts.builder()
                .setHeader(header)
                .claims(payload)
                .signWith(
                        SignatureAlgorithm.HS256,
                        Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))
                )
                .compact();
    }

    public boolean authorizationUserRights(Cookie[] cookies) {
        if (cookies == null)
            return false;

        Optional<Cookie> jwt = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("JWT"))
                .findFirst();

        return jwt.isPresent() && isTokenValid(jwt.get().getValue());
    }

    public boolean authorizationUserRights(Cookie[] cookies, String id) {
        if (cookies == null)
            return false;

        Optional<Cookie> jwt = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("JWT"))
                .findFirst();

        if (jwt.isPresent() && isTokenValid(jwt.get().getValue())) {
            Object o = extractPayload(jwt.get().getValue()).getOrDefault("userId", "error");
            return o.toString().equals(id);
        }

        return false;
    }

    public boolean authorizationAdminRights(Cookie[] cookies) {
        if (cookies == null)
            return false;

        Optional<Cookie> jwt = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("JWT"))
                .findFirst();

        return jwt.isPresent() && isTokenValidForAdmin(jwt.get().getValue());
    }

    public boolean isTokenValid(String actualToken) {
        JwsHeader jwsHeader = extractHeader(actualToken);
        Claims payload = extractPayload(actualToken);

        String expectedToken = Jwts.builder()
                .setHeader(jwsHeader)
                .claims(payload)
                .signWith(
                        SignatureAlgorithm.HS256,
                        Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY))
                )
                .compact();

        return expectedToken.equals(actualToken);
    }

    public boolean isTokenValidForAdmin(String token) {
        Object o = extractPayload(token).getOrDefault("role", "USER");

        return o.toString().equals("ADMIN");
    }

    public JwsHeader extractHeader(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getHeader();
    }

    public Claims extractPayload(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }
}