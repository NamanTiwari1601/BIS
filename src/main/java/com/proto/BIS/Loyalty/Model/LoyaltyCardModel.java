package com.proto.BIS.Loyalty.Model;

import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Model.ServicesModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "s_loyalty_card")
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "s_loyalty_card_service",
            joinColumns = @JoinColumn(name = "loyalty_card_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    @OrderBy("serviceId ASC")
    private Set<ServicesModel> services = new LinkedHashSet<>();

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

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
