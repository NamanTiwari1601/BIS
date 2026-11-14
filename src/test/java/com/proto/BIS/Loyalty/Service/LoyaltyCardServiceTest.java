package com.proto.BIS.Loyalty.Service;

import com.proto.BIS.Loyalty.DTO.LoyaltyCardRequest;
import com.proto.BIS.Loyalty.Model.LoyaltyCardModel;
import com.proto.BIS.Loyalty.Repository.LoyaltyCardRepository;
import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.ClientRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyCardServiceTest {
    @Mock LoyaltyCardRepository loyaltyCardRepository;
    @Mock ClientRepo clientRepository;
    @Mock WorkRepo serviceRepository;
    @InjectMocks LoyaltyCardService loyaltyCardService;

    @Test
    void createsClientAndServiceCard() {
        LoyaltyCardRequest request = new LoyaltyCardRequest();
        request.setClientName("Riya Sharma");
        request.setClientPhone("1234567890");
        request.setDateOfBirth(LocalDate.of(1995, 5, 12));
        request.setServiceId(4L);

        when(clientRepository.findByClientPhone("1234567890")).thenReturn(Optional.empty());
        when(clientRepository.save(any(ClientModel.class))).thenAnswer(invocation -> {
            ClientModel client = invocation.getArgument(0);
            client.setClientId(7);
            return client;
        });
        when(serviceRepository.findById(4)).thenReturn(Optional.of(
                ServicesModel.builder().serviceId(4L).serviceName("Haircut").build()));
        when(loyaltyCardRepository.existsByClient_ClientIdAndService_ServiceId(7, 4L)).thenReturn(false);
        when(loyaltyCardRepository.save(any(LoyaltyCardModel.class))).thenAnswer(invocation -> {
            LoyaltyCardModel card = invocation.getArgument(0);
            card.setLoyaltyCardId(10L);
            return card;
        });

        var response = loyaltyCardService.create(request);

        assertEquals(10L, response.getLoyaltyCardId());
        assertEquals(7L, response.getClientId());
        assertEquals("Riya Sharma", response.getClientName());
        assertEquals(LocalDate.of(1995, 5, 12), response.getDateOfBirth());
        assertEquals("Haircut", response.getServiceName());
        assertEquals(0, response.getPaidVisits());
    }

    @Test
    void reusesMatchingClientAndFillsMissingDateOfBirth() {
        LoyaltyCardRequest request = new LoyaltyCardRequest();
        request.setClientName("Riya Sharma");
        request.setClientPhone("1234567890");
        request.setDateOfBirth(LocalDate.of(1995, 5, 12));
        request.setServiceId(4L);

        ClientModel existingClient = new ClientModel();
        existingClient.setClientId(7);
        existingClient.setClientName("Riya Sharma");
        existingClient.setClientPhone("1234567890");
        when(clientRepository.findByClientPhone("1234567890")).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(existingClient)).thenReturn(existingClient);
        when(serviceRepository.findById(4)).thenReturn(Optional.of(
                ServicesModel.builder().serviceId(4L).serviceName("Haircut").build()));
        when(loyaltyCardRepository.existsByClient_ClientIdAndService_ServiceId(7, 4L)).thenReturn(false);
        when(loyaltyCardRepository.save(any(LoyaltyCardModel.class))).thenAnswer(invocation -> {
            LoyaltyCardModel card = invocation.getArgument(0);
            card.setLoyaltyCardId(10L);
            return card;
        });

        var response = loyaltyCardService.create(request);

        assertEquals(7L, response.getClientId());
        assertEquals("Riya Sharma", response.getClientName());
        assertEquals(LocalDate.of(1995, 5, 12), existingClient.getDateOfBirth());
        verify(clientRepository).save(existingClient);
    }

    @Test
    void allowsNinthPaidVisitButRejectsTenthAsPaid() {
        LoyaltyCardModel card = cardWithPaidVisits(8);
        when(loyaltyCardRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(card));

        assertEquals(9, loyaltyCardService.recordPaidVisit(10L).getPaidVisits());
        assertThrows(IllegalArgumentException.class, () -> loyaltyCardService.recordPaidVisit(10L));
    }

    @Test
    void requiresNinePaidVisitsBeforeRecordingFreeVisitAndStartsNextCycle() {
        LoyaltyCardModel card = cardWithPaidVisits(8);
        when(loyaltyCardRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(card));

        assertThrows(IllegalArgumentException.class, () -> loyaltyCardService.recordFreeVisit(10L));

        card.setPaidVisits(9);
        var response = loyaltyCardService.recordFreeVisit(10L);
        assertEquals(0, response.getPaidVisits());
        assertEquals(1, response.getFreeVisits());
    }

    private LoyaltyCardModel cardWithPaidVisits(int paidVisits) {
        ClientModel client = new ClientModel();
        client.setClientId(7);
        client.setClientName("Riya Sharma");
        client.setDateOfBirth(LocalDate.of(1995, 5, 12));
        ServicesModel service = ServicesModel.builder().serviceId(4L).serviceName("Haircut").build();
        LoyaltyCardModel card = new LoyaltyCardModel();
        card.setLoyaltyCardId(10L);
        card.setClient(client);
        card.setService(service);
        card.setPaidVisits(paidVisits);
        return card;
    }
}
