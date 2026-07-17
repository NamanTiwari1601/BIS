package com.proto.BIS.Billing.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {

    private Integer billId;
    private LocalDateTime billDate;
    private Double totalAmount;

    private Integer staffId;
    private String staffName;

    private List<BillItemResponseDTO> items;
}
