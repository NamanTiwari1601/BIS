package com.proto.BIS.common.Service;

import com.proto.BIS.common.DTO.ClientRequest;
import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Repository.ClientRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock ClientRepo repo;
    @InjectMocks ClientService service;

    @Test
    void addsClientWithTrimmedFieldsAndDefaultStatus() {
        ClientRequest request = new ClientRequest(" Alice ", "1234567890", " a@b.com ", null, "note");
        when(repo.findByClientPhone("1234567890")).thenReturn(Optional.empty());
        when(repo.save(any(ClientModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.addClient(request);

        assertEquals("Alice", response.getClientName());
        assertEquals("1234567890", response.getClientPhone());
        assertEquals("a@b.com", response.getClientEmail());
        assertEquals("Active", response.getStatus());
    }

    @Test
    void validatesPhoneAndDuplicateClient() {
        ClientRequest invalid = new ClientRequest("Alice", "123", null, null, null);
        assertThrows(IllegalArgumentException.class, () -> service.addClient(invalid));
        ClientRequest duplicate = new ClientRequest("Alice", "1234567890", null, null, null);
        when(repo.findByClientPhone("1234567890")).thenReturn(Optional.of(new ClientModel()));
        assertThrows(IllegalArgumentException.class, () -> service.addClient(duplicate));
    }
}