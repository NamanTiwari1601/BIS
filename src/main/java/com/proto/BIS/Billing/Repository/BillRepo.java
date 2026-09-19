package com.proto.BIS.Billing.Repository;

import com.proto.BIS.Billing.Model.Bills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepo extends JpaRepository<Bills,Integer> {
    List<Bills> findByBillDateBetween(LocalDateTime start,LocalDateTime end);
    List<Bills> findByStaffUserId(Integer staffId);
    List<Bills> findByStaffUserIdAndBillDateBetween(Integer staffId, LocalDateTime start, LocalDateTime end);
}

