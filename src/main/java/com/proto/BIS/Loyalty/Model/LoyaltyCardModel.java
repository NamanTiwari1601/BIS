package com.proto.BIS.Loyalty.Model;

import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Model.ServicesModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "s_loyalty_card", uniqueConstraints = @UniqueConstraint(
        name = "uq_loyalty_card_client_service",
        columnNames = {"client_id", "service_id"}))
@Getter
@Setter
@NoArgsConstructor
public class LoyaltyCardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loyalty_card_id")
    private Long loyaltyCardId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientModel client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServicesModel service;

    @Column(name = "paid_visits", nullable = false)
    private int paidVisits;

    @Column(name = "free_visits", nullable = false)
    private int freeVisits;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
