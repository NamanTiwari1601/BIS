package com.proto.BIS.common.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Entity
@Table(name="s_product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long productId;

        @NotBlank(message = "Product Name cannot be null")
        private String productName;

        private String discription;

        private double productPrice;

        private Long productQuantity;

        @CreationTimestamp
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;

    }