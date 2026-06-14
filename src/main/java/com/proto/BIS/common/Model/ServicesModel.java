package com.proto.BIS.common.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicesModel {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int serviceId;
    private String serviceName;
    private double servicePrice;

}
