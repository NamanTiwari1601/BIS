package com.proto.BIS.Loyalty.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

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
    @Positive
    private Long serviceId;
}
