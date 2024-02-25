package com.shaikhraziev.service;

import com.shaikhraziev.dto.UserReadDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shaikhraziev.entity.Role.ADMIN;
import static com.shaikhraziev.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    private final UserReadDto TEST_USER_READ_DTO = UserReadDto.builder()
            .id(5L)
            .username("katya")
            .role(USER)
            .build();

    private static final String JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJJZCI6NDB9.eMH_2fdgkdv3kBhXjfrwLC6GKVcfzc-Yb_1Tpi5H-jg";

    @Test
    @DisplayName("token should be generated")
    void generateToken() {
        String actualResult = jwtService.generateToken(TEST_USER_READ_DTO);

        assertThat(actualResult).isNotNull();
    }

    @Test
    @DisplayName("should return true to the authorized user")
    void authorizationUserRights() {
        Cookie[] cookies = new Cookie[]{new Cookie("JWT", JWT_TOKEN)};

        boolean actualResult = jwtService.authorizationUserRights(cookies);

        assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("should return true to the user who has not been authorized")
    void testAuthorizationUserRights() {
        Cookie[] cookies = new Cookie[]{new Cookie("JWT", JWT_TOKEN)};

        boolean actualResult = jwtService.authorizationUserRights(cookies, "1");

        assertThat(actualResult).isFalse();
    }

    @Test
    @DisplayName("should return true to the administrator who has not been authorized")
    void authorizationAdminRights() {
        boolean actualResult = jwtService.authorizationAdminRights(null);

        assertThat(actualResult).isFalse();
    }

    @Test
    @DisplayName("should return true to a valid token")
    void isTokenValid() {
        UserReadDto user = UserReadDto.builder()
                .id(100L)
                .username("User")
                .role(USER)
                .build();

        String token = jwtService.generateToken(user);

        boolean actualResult = jwtService.isTokenValid(token);

        assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("should return true to a valid token")
    void isTokenValidForAdmin() {
        UserReadDto admin = UserReadDto.builder()
                .id(100L)
                .username("Admin")
                .role(ADMIN)
                .build();

        String token = jwtService.generateToken(admin);

        boolean actualResult = jwtService.isTokenValidForAdmin(token);

        assertThat(actualResult).isTrue();
    }

    @Test
    @DisplayName("should get headers with typ = JWT")
    void extractHeader() {
        JwsHeader jwsHeader = jwtService.extractHeader(JWT_TOKEN);
        String actualResult = jwsHeader.getOrDefault("typ", "error").toString();

        assertThat(actualResult).isEqualTo("JWT");
    }

    @Test
    @DisplayName("should get claims with role = USER")
    void extractPayload() {
        Claims claims = jwtService.extractPayload(JWT_TOKEN);
        String actualResult = claims.getOrDefault("role", "error").toString();

        assertThat(actualResult).isEqualTo("USER");
    }
}