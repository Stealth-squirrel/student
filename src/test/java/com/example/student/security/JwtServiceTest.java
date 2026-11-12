package com.example.student.security;

import com.example.student.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_and_extractUsername_shouldWork() {
        String token = jwtService.generateToken("admin@test.com", Set.of(Role.ADMIN));
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("admin@test.com", username);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("admin@test.com", Set.of(Role.ADMIN));
        UserDetails userDetails = User.withUsername("admin@test.com").password("123").roles("ADMIN").build();

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalseForWrongUser() {
        String token = jwtService.generateToken("admin@test.com", Set.of(Role.ADMIN));
        UserDetails userDetails = User.withUsername("other@test.com").password("123").roles("ADMIN").build();

        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void extractRoles_shouldReturnRoles() {
        String token = jwtService.generateToken("admin@test.com", Set.of(Role.ADMIN, Role.TEACHER));
        Set<Role> roles = jwtService.extractRoles(token);

        assertTrue(roles.contains(Role.ADMIN));
        assertTrue(roles.contains(Role.TEACHER));
    }
}