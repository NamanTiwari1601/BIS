package com.proto.BIS.Billing.DTO;

import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServicesModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillItemResponseDTO {

    private Integer billItemId;

    private Integer billId;

    private String productName;
    private String serviceName;
    private Integer quantity;
    private Double subTotal;

}
