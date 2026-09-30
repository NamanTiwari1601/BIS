package com.proto.BIS.Billing.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Entity
@Table(name = "s_dues")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DuesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer duesId;

    @NotBlank
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    private String clientName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "invalid phone number")
    private String clientPhone;

    private Double totalAmount;
    private  Double paidAmount;
    private Double dueAmount;

    private LocalDate duedate;

    @Pattern(regexp = "PAID|PARTIAL|PENDING")
    private String status;

    private String notes;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bills bill;


}
