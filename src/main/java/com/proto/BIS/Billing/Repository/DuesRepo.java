package com.proto.BIS.Billing.Repository;

import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Model.DuesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DuesRepo extends JpaRepository<DuesModel, Integer> {
    List<DuesModel> findByStatus(String status);
    List<DuesModel> findByStatusIn(List<String> statuses);
    List<DuesModel> findByClientPhone(String phone);
    List<DuesModel> findByBill(Bills bill);
    List<DuesModel> findByBill_BillId(Integer billId);
}
