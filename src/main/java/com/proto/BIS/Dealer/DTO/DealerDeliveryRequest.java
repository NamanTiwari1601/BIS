package com.proto.BIS.Dealer.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DealerDeliveryRequest {
    @Positive
    private Long billId;

    @NotNull
    private LocalDate deliveryDate;

    @NotBlank
    private String status;

    @Size(max = 1000)
    private String notes;
}
