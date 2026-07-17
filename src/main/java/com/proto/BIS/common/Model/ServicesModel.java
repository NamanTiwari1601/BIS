package com.proto.BIS.common.Model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Audited;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "s_service")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicesModel {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @NotBlank(message = "Service Name cannot be null")
    private String serviceName;


    private double servicePrice;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
