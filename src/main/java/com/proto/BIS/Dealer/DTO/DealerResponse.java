package com.proto.BIS.Dealer.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerResponse {
    private Long dealerId;
    private String name;
    private String phone;
    private BigDecimal totalDue;
    private List<ProductResponse> products;
    private List<BillResponse> bills;
    private List<DeliveryResponse> deliveries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long productId;
        private String productName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillResponse {
        private Long billId;
        private String invoiceNumber;
        private LocalDate billDate;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal dueAmount;
        private String notes;
        private List<BillItemResponse> items;
        private List<PaymentResponse> payments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillItemResponse {
        private Long billItemId;
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private Long paymentId;
        private BigDecimal amount;
        private LocalDate paymentDate;
        private String method;
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryResponse {
        private Long deliveryId;
        private Long billId;
        private String invoiceNumber;
        private LocalDate deliveryDate;
        private String status;
        private String notes;
    }
}
