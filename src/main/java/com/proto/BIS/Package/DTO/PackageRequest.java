package com.proto.BIS.Package.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PackageRequest {
    @NotBlank
    private String packageName;
    private String description;
    private String category;
    @DecimalMin("0.0")
    private double packagePrice;
    @Min(1)
    private int durationMinutes;
    private String status = "ACTIVE";
    @NotEmpty
    private List<ServiceItemRequest> services;

    @Data
    public static class ServiceItemRequest {
        private Long serviceId;
        @Min(1)
        private int quantity = 1;
    }
}