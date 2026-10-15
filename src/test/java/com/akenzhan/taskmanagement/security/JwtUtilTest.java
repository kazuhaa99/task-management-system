package com.akenzhan.taskmanagement.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-32-characters-long";

    private JwtUtil jwtUtil;
    private UserDetails alice;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60_000L);
        alice = User.withUsername("alice").password("pw").authorities("ROLE_USER").build();
    }

    @Test
    void generatedTokenContainsUsernameAndIsValid() {
        String token = jwtUtil.generateToken(alice);

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtUtil.validateToken(token, alice)).isTrue();
    }

    @Test
    void tokenIsRejectedForAnotherUser() {
        String token = jwtUtil.generateToken(alice);
        UserDetails bob = User.withUsername("bob").password("pw").authorities("ROLE_USER").build();

        assertThat(jwtUtil.validateToken(token, bob)).isFalse();
    }

    @Test
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1_000L);
        String token = jwtUtil.generateToken(alice);

        assertThat(jwtUtil.validateToken(token, alice)).isFalse();
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        JwtUtil otherIssuer = new JwtUtil();
        ReflectionTestUtils.setField(otherIssuer, "secret", "another-secret-key-that-is-at-least-32-chars");
        ReflectionTestUtils.setField(otherIssuer, "expiration", 60_000L);
        String forged = otherIssuer.generateToken(alice);

        assertThat(jwtUtil.validateToken(forged, alice)).isFalse();
    }
}
