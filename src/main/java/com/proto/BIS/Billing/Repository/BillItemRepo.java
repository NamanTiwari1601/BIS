package com.proto.BIS.Billing.Repository;

import com.proto.BIS.Billing.Model.BillItemModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillItemRepo extends JpaRepository<BillItemModel,Integer> {
    List<BillItemModel> findByBillBillId(int billId);
}
