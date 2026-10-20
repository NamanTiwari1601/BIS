package com.proto.BIS.common.Service;

import com.proto.BIS.common.DTO.AuthRequest;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepo repo;
    @Mock JwtUtil jwtUtil;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AuthService service;

    @Test
    void loginReturnsTokenForValidCredentials() {
        UserModel user = new UserModel();
        user.setUserName("alice");
        user.setUserPass("encoded");
        user.setUserAdmin(true);
        AuthRequest request = new AuthRequest();
        request.setUserName("alice");
        request.setUserPass("plain");
        when(repo.findByUserName("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken("alice", true)).thenReturn("token");

        var response = service.login(request);

        assertEquals("token", response.getToken());
        assertEquals("alice", response.getUserName());
        assertTrue(response.getIsAdmin());
    }

    @Test
    void loginRejectsUnknownUserAndBadPassword() {
        AuthRequest request = new AuthRequest();
        request.setUserName("missing");
        when(repo.findByUserName("missing")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.login(request));

        UserModel user = new UserModel();
        user.setUserName("alice");
        user.setUserPass("encoded");
        request.setUserName("alice");
        request.setUserPass("wrong");
        when(repo.findByUserName("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encoded"))).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.login(request));
    }
}