package com.proto.BIS.Loyalty.DTO;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class LoyaltyCardResponse {
    Long loyaltyCardId;
    Long clientId;
    String clientName;
    String clientPhone;
    LocalDate dateOfBirth;
    LocalDate expiryDate;
    List<Long> serviceIds;
    List<ServiceInfo> services;
    Long serviceId;
    String serviceName;
    int paidVisits;
    int freeVisits;

    @Value
    @Builder
    public static class ServiceInfo {
        Long serviceId;
        String serviceName;
    }
}
