package com.orderservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "jwt.secret=test-secret-key-minimum-32-characters-long!!!",
        "jwt.expirationMs=3600000"
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("generateToken produces valid token with subject and role")
    void generateToken_containsUsernameAndRole() {
        String token = jwtUtil.generateToken("keerthana", "USER");

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("keerthana");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("USER");
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("extractUsername returns subject from token")
    void extractUsername_returnsSubject() {
        String token = jwtUtil.generateToken("admin", "ADMIN");
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    @DisplayName("extractRole returns role claim")
    void extractRole_returnsRoleClaim() {
        String token = jwtUtil.generateToken("admin", "ADMIN");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("isTokenValid returns false for invalid token")
    void isTokenValid_returnsFalseForInvalidToken() {
        assertThat(jwtUtil.isTokenValid("invalid.token.here")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid returns false for empty string")
    void isTokenValid_returnsFalseForEmpty() {
        assertThat(jwtUtil.isTokenValid("")).isFalse();
    }
}
