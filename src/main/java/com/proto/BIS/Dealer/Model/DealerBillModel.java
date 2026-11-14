package com.proto.BIS.Dealer.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "s_dealer_bill")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerBillModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private DealerModel dealer;

    @Column(nullable = false, length = 100)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate billDate;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal paidAmount;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal dueAmount;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("billItemId ASC")
    @Builder.Default
    private List<DealerBillItemModel> items = new ArrayList<>();

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("paymentDate ASC, paymentId ASC")
    @Builder.Default
    private List<DealerPaymentModel> payments = new ArrayList<>();

    @PrePersist
    private void setCreatedAt() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
