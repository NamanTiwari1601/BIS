package com.proto.BIS.Loyalty.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LoyaltyCardRequest {
    @NotBlank
    @Size(max = 255)
    private String clientName;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String clientPhone;

    @NotNull
    @PastOrPresent
    private LocalDate dateOfBirth;

    @NotNull
    @FutureOrPresent
    private LocalDate expiryDate;

    @Size(max = 100)
    private List<@NotNull @Positive Long> serviceIds;

    @Positive
    private Long serviceId;

    @AssertTrue(message = "Select at least one service")
    public boolean isServiceSelectionValid() {
        return (serviceIds != null && !serviceIds.isEmpty()) || serviceId != null;
    }
}
