package com.proto.BIS.Loyalty.Service;

import com.proto.BIS.Loyalty.DTO.LoyaltyCardRequest;
import com.proto.BIS.Loyalty.DTO.LoyaltyCardResponse;
import com.proto.BIS.Loyalty.DTO.LoyaltyCardResponse.ServiceInfo;
import com.proto.BIS.Loyalty.Model.LoyaltyCardModel;
import com.proto.BIS.Loyalty.Repository.LoyaltyCardRepository;
import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.ClientRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class LoyaltyCardService {
    private static final int PAID_VISITS_REQUIRED = 9;

    private final LoyaltyCardRepository loyaltyCardRepository;
    private final ClientRepo clientRepository;
    private final WorkRepo serviceRepository;

    @Transactional(readOnly = true)
    public List<LoyaltyCardResponse> getAll() {
        return loyaltyCardRepository.findAllByOrderByLoyaltyCardIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public LoyaltyCardResponse create(LoyaltyCardRequest request) {
        String clientName = request.getClientName().trim();
        String clientPhone = request.getClientPhone().trim();

        ClientModel client = clientRepository.findByClientPhone(clientPhone)
                .map(existing -> useExistingClient(existing, clientName, request.getDateOfBirth()))
                .orElseGet(() -> createClient(clientName, clientPhone, request.getDateOfBirth()));

        LoyaltyCardModel card = new LoyaltyCardModel();
        card.setClient(client);
        card.setExpiryDate(request.getExpiryDate());
        card.setServices(resolveServices(request));
        return toResponse(loyaltyCardRepository.save(card));
    }

    public LoyaltyCardResponse update(Long id, LoyaltyCardRequest request) {
        LoyaltyCardModel card = findForUpdate(id);
        updateClient(card.getClient(), request);
        card.setExpiryDate(request.getExpiryDate());
        card.setServices(resolveServices(request));
        return toResponse(loyaltyCardRepository.save(card));
    }

    public LoyaltyCardResponse recordPaidVisit(Long id) {
        LoyaltyCardModel card = findForUpdate(id);
        if (card.getPaidVisits() >= PAID_VISITS_REQUIRED) {
            throw new IllegalArgumentException("The tenth visit is free. Record it as a free visit instead");
        }
        card.setPaidVisits(card.getPaidVisits() + 1);
        return toResponse(card);
    }

    public LoyaltyCardResponse recordFreeVisit(Long id) {
        LoyaltyCardModel card = findForUpdate(id);
        if (card.getPaidVisits() != PAID_VISITS_REQUIRED) {
            throw new IllegalArgumentException("The free tenth visit is available after 9 paid visits");
        }
        card.setPaidVisits(0);
        card.setFreeVisits(card.getFreeVisits() + 1);
        return toResponse(card);
    }

    private ClientModel useExistingClient(ClientModel client, String clientName, java.time.LocalDate dateOfBirth) {
        if (!client.getClientName().trim().equalsIgnoreCase(clientName)) {
            throw new IllegalArgumentException("This phone number already belongs to a different client");
        }
        if (client.getDateOfBirth() != null && !client.getDateOfBirth().equals(dateOfBirth)) {
            throw new IllegalArgumentException("The date of birth does not match the existing client");
        }
        if (client.getDateOfBirth() == null) {
            client.setDateOfBirth(dateOfBirth);
            return clientRepository.save(client);
        }
        return client;
    }

    private ClientModel createClient(String name, String phone, java.time.LocalDate dateOfBirth) {
        ClientModel client = new ClientModel();
        client.setClientName(name);
        client.setClientPhone(phone);
        client.setDateOfBirth(dateOfBirth);
        client.setStatus("Active");
        return clientRepository.save(client);
    }

    private void updateClient(ClientModel client, LoyaltyCardRequest request) {
        String clientName = request.getClientName().trim();
        String clientPhone = request.getClientPhone().trim();
        clientRepository.findByClientPhone(clientPhone)
                .filter(phoneOwner -> !Objects.equals(phoneOwner.getClientId(), client.getClientId()))
                .ifPresent(phoneOwner -> {
                    throw new IllegalArgumentException("This phone number already belongs to a different client");
                });
        client.setClientName(clientName);
        client.setClientPhone(clientPhone);
        client.setDateOfBirth(request.getDateOfBirth());
        clientRepository.save(client);
    }

    private Set<ServicesModel> resolveServices(LoyaltyCardRequest request) {
        List<Long> serviceIds = request.getServiceIds();
        if (serviceIds == null || serviceIds.isEmpty()) {
            serviceIds = request.getServiceId() == null ? List.of() : List.of(request.getServiceId());
        }
        if (serviceIds.isEmpty()) {
            throw new IllegalArgumentException("Select at least one service");
        }
        if (new HashSet<>(serviceIds).size() != serviceIds.size()) {
            throw new IllegalArgumentException("A service can only be selected once");
        }

        Set<ServicesModel> services = new LinkedHashSet<>();
        for (Long serviceId : serviceIds) {
            if (serviceId == null || serviceId <= 0 || serviceId > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Invalid service ID");
            }
            services.add(serviceRepository.findById(serviceId.intValue())
                    .orElseThrow(() -> new EntityNotFoundException("Service not found: " + serviceId)));
        }
        return services;
    }

    private LoyaltyCardModel findForUpdate(Long id) {
        return loyaltyCardRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Loyalty card not found: " + id));
    }

    private LoyaltyCardResponse toResponse(LoyaltyCardModel card) {
        ClientModel client = card.getClient();
        List<ServiceInfo> services = card.getServices().stream()
                .sorted((first, second) -> first.getServiceId().compareTo(second.getServiceId()))
                .map(service -> ServiceInfo.builder()
                        .serviceId(service.getServiceId())
                        .serviceName(service.getServiceName())
                        .build())
                .toList();
        ServiceInfo firstService = services.isEmpty() ? null : services.get(0);
        return LoyaltyCardResponse.builder()
                .loyaltyCardId(card.getLoyaltyCardId())
                .clientId(client.getClientId().longValue())
                .clientName(client.getClientName())
                .clientPhone(client.getClientPhone())
                .dateOfBirth(client.getDateOfBirth())
                .expiryDate(card.getExpiryDate())
                .serviceIds(services.stream().map(ServiceInfo::getServiceId).toList())
                .services(services)
                .serviceId(firstService == null ? null : firstService.getServiceId())
                .serviceName(firstService == null ? null : firstService.getServiceName())
                .paidVisits(card.getPaidVisits())
                .freeVisits(card.getFreeVisits())
                .build();
    }
}
