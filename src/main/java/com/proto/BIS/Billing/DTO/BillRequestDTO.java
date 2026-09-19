package com.proto.BIS.Billing.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillRequestDTO {
    private Integer staffId;
    private Integer billAmount;
    private Integer billDues;
    private String clientName;
    private String emailId;
    private String clientPhone;
    private String notes;
    private String paymentMode;
    private Double discountPercent;
    private Double gstPercent;
    private List<BillItemRequestDTO> items;


}
