package com.proto.BIS.Package.DTO;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class PackageResponse {
    Long packageId;
    String packageName;
    String description;
    String category;
    double packagePrice;
    int durationMinutes;
    String status;
    LocalDateTime createdAt;
    List<ServiceResponse> services;

    @Value
    @Builder
    public static class ServiceResponse {
        Long serviceId;
        String serviceName;
        int quantity;
        double price;
    }
}