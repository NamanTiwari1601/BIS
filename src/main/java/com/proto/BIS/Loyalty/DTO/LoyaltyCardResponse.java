package com.proto.BIS.Loyalty.DTO;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class LoyaltyCardResponse {
    Long loyaltyCardId;
    Long clientId;
    String clientName;
    String clientPhone;
    LocalDate dateOfBirth;
    Long serviceId;
    String serviceName;
    int paidVisits;
    int freeVisits;
}
