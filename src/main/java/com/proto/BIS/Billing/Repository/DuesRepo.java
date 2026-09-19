package com.proto.BIS.Billing.Repository;


import com.proto.BIS.Billing.Model.DuesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface DuesRepo extends JpaRepository<DuesModel, Integer> {
    List<DuesModel> findByStatus(String status);
    List<DuesModel> findByClientPhone(String phone);
    List<DuesModel> findByBill(Integer billId);

}
