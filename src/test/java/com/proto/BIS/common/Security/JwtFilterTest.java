package com.proto.BIS.common.Security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtFilterTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void grantsAdminAuthorityOnlyWhenTokenClaimsAdmin() throws ServletException, IOException {
        assertEquals(List.of("ROLE_ADMIN"), authoritiesFor(true));
        assertEquals(List.of(), authoritiesFor(false));
    }

    private List<String> authoritiesFor(boolean isAdmin) throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.isTokenValid("token")).thenReturn(true);
        when(jwtUtil.extractUserName("token")).thenReturn("operator");
        when(jwtUtil.extractIsAdmin("token")).thenReturn(isAdmin);
        JwtFilter filter = new JwtFilter();
        ReflectionTestUtils.setField(filter, "jwtUtil", jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
    }
}
