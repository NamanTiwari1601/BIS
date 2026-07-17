package com.proto.BIS.Billing.Model;

import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServicesModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="s_bill_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billItemId;

    @ManyToOne
    @JoinColumn(name="bill_id")
    private Bills bill;

    @ManyToOne
    @JoinColumn(name="product_id")
    private ProductModel product;

    @ManyToOne
    @JoinColumn(name="service_id")
    private ServicesModel services;

    private int  quantity;

    private  double subtotal;
}   
