package com.proto.BIS.Dealer.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class DealerBillRequest {
    @NotBlank
    @Size(max = 100)
    private String invoiceNumber;

    @NotNull
    private LocalDate billDate;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.00")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Size(max = 1000)
    private String notes;

    @NotEmpty
    @Valid
    private List<ItemRequest> items;

    @Data
    public static class ItemRequest {
        @NotNull
        @Positive
        private Long productId;

        @Positive
        private int quantity;

        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 12, fraction = 2)
        private BigDecimal unitPrice;
    }
}
