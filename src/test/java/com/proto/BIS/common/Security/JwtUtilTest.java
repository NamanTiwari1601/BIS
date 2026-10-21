package com.proto.BIS.common.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private JwtUtil jwt;

    @BeforeEach
    void setUp() throws Exception {
        jwt = new JwtUtil();
        Field field = JwtUtil.class.getDeclaredField("jwtSecret");
        field.setAccessible(true);
        field.set(jwt, "01234567890123456789012345678901");
    }

    @Test
    void generatesAndReadsValidToken() {
        String token = jwt.generateToken("alice", true);
        assertTrue(jwt.isTokenValid(token));
        assertEquals("alice", jwt.extractUserName(token));
        assertTrue(jwt.extractIsAdmin(token));
    }

    @Test
    void rejectsMalformedToken() {
        assertFalse(jwt.isTokenValid("not-a-token"));
    }
}