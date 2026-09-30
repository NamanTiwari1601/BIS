package com.proto.BIS.Billing.DTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillRequestDTO {
    private Integer staffId;
    private Double totalAmount;
    private Double dueAmount;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String notes;
    private String paymentMode;
    private Double discountPercent;
    private Double gstPercent;
    private String paymentStatus;
    private LocalDate dueDate;
    private List<BillItemRequestDTO> items;


}