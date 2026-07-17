package com.proto.BIS.Billing.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillItemRequestDTO {
        private Integer productId;
        private Integer serviceId;
        private Integer quantity;

}
