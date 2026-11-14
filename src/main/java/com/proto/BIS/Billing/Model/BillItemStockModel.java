package com.proto.BIS.Billing.Model;

import com.proto.BIS.common.Model.ProductModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "s_bill_item_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItemStockModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockUsageId;

    @ManyToOne
    @JoinColumn(name = "bill_item_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BillItemModel billItem;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductModel product;

    @Column(nullable = false)
    private long quantity;
}
