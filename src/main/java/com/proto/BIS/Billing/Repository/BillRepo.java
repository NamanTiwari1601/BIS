package com.proto.BIS.Billing.Repository;

import com.proto.BIS.Billing.Model.Bills;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepo extends JpaRepository<Bills,Integer> {
    List<Bills> findByBillDateBetween(LocalDateTime start,LocalDateTime end);
    Page<Bills> findByBillDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<Bills> findByStaffUserId(Integer staffId);
    Page<Bills> findByStaffUserId(Integer staffId, Pageable pageable);
    List<Bills> findByStaffUserIdAndBillDateBetween(Integer staffId, LocalDateTime start, LocalDateTime end);
    Page<Bills> findByStaffUserIdAndBillDateBetween(Integer staffId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Bills> findAll(Pageable pageable);
}

