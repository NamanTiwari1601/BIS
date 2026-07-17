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
    private List<BillItemRequestDTO> items;

}
